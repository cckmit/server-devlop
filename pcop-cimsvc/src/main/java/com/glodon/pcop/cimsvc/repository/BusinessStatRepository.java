package com.glodon.pcop.cimsvc.repository;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BusinessStatRepository {

	@Autowired
	private ObjectMapper mapper;

	private static final Logger log = LoggerFactory.getLogger(BusinessStatRepository.class);



	public List<MatchResult> multiObjectGeneralGroupQueryMatch(String match, String groupByName,String containName) {
		CimDataSpace cds = null;
		try {
			cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			OrientDBCimDataSpaceImpl orientDBCimDataSpaceImpl = (OrientDBCimDataSpaceImpl) cds;
			ODatabaseDocumentTx db = orientDBCimDataSpaceImpl.getGraph().getRawGraph();
			List<OElement> results = executeIdempotentQuery(db, match);
			List<Map<String, String>> mapList = new ArrayList<>();
			for (OElement element : results) {
				HashMap<String, String> hashMap = mapper.readValue(element.toJSON("rid"), HashMap.class);
				for (Map.Entry<String, String> entry : hashMap.entrySet()) {
					if (entry.getValue() == null) {
						entry.setValue("null");
					}
				}
				mapList.add(hashMap);
			}

			Map<String, Set<String>> businessBuilding = mapList.stream().collect(Collectors.groupingBy(e -> e.remove(groupByName), Collectors.mapping(e -> e.remove(containName), Collectors.toSet())));
			List<MatchResult> matchResults = new ArrayList<>();

			Set<Map.Entry<String, Set<String>>> entries = businessBuilding.entrySet();
			for (Map.Entry<String, Set<String>> entry : entries) {
				MatchResult matchResult = new MatchResult();
				String bbRid = entry.getKey();
				Set<String> value = entry.getValue();
				getBaseInfoByRid(bbRid, cds, matchResult);
				Set<Map<String, Object>> set = new HashSet<>();

				if(matchResult != null){
					for (String rid : value) {
						Map<String, Object> properties = getPropertiesByRid(rid, cds, matchResult);
						set.add(properties);
					}
				}
				matchResult.setContainedObjectPropertiesSet(set);
				matchResults.add(matchResult);
			}

			return matchResults;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (cds != null) {
				cds.closeSpace();
			}
		}
		return null;
	}
	private Map<String, Object>  getPropertiesByRid(String rid, CimDataSpace cds, MatchResult matchResult) {
		Fact targetFact = null;
		try {
			targetFact = cds.getFactById(rid);
			String infoObjectTypeDefStr = targetFact.getType();
			matchResult.setContainedObjectType(infoObjectTypeDefStr);
			CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, null);
			modelCore.setCimDataSpace(cds);
			InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(infoObjectTypeDefStr);
			InfoObject infoObject = infoObjectDef.getObject(rid);
			Map<String, Object> baseInfo = infoObject.getInfo();
			Map<String, Object> mergedMap = new HashMap<>();
			mergedMap.putAll(baseInfo);
			Map<String, Map<String, Object>> propertiesByDatasets = infoObject.getObjectPropertiesByDatasets();
			for (Map.Entry<String, Map<String, Object>> mapEntry : propertiesByDatasets.entrySet()) {
				Map<String, Object> generalInfo = mapEntry.getValue();
				mergedMap.putAll(generalInfo);
			}


			return mergedMap;
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} catch (DataServiceModelRuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	private MatchResult getBaseInfoByRid(String bbRid, CimDataSpace cds,MatchResult matchResult) {
		try {
			if(!bbRid.equals("null")){
				Fact targetFact = cds.getFactById(bbRid);
				String infoObjectTypeDefStr = targetFact.getType();
				matchResult.setObjectType(infoObjectTypeDefStr);
				CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, null);
				modelCore.setCimDataSpace(cds);

				InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(infoObjectTypeDefStr);
				InfoObject infoObject = infoObjectDef.getObject(bbRid);
				Map<String, Object> info = infoObject.getInfo();
				matchResult.setObjectTypeProperties(info);
				return matchResult;
			}else {
				Map<String, Object> nullMap = new HashMap<>();
				nullMap.put("ID","null");
				matchResult.setObjectType("null");
				matchResult.setObjectTypeProperties(nullMap);

				return matchResult;
			}


		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} catch (DataServiceModelRuntimeException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static List<OElement> executeIdempotentQuery(ODatabaseDocument db, StringBuilder query) {
		return executeIdempotentQuery(db, query.toString());
	}

	public static List<OElement> executeIdempotentQuery(ODatabaseDocument db, String query) {
		return executeIdempotentQueryWithParams(db, query, Collections.<String, Object>emptyMap());
	}

	public static List<OElement> executeIdempotentQueryWithParams(ODatabaseDocument db, String query, Map<String, Object> queryParams) {
		List<OElement> results = new ArrayList<>();
		try (OResultSet resultSet = db.query(query, queryParams)) {

			while (resultSet.hasNext()) {
				OElement element = resultSet.next().toElement();
				results.add(element);
			}


		}
		return results;
	}

}
