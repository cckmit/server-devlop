package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.junit.*;

import java.util.*;


@Ignore
public class TreeServiceTest2 {


	DataPermissionService permissionService = new DataPermissionService();


	private static String sourceTreeName = "DATA_MANAGER_CONTENT_TREE";

	private static String targetTreeName = "mmmmmm";

	private CIMModelCore cimModelCore;

	private String userId = "6435737162427609322";

	@Before
	public void setup() {
		PropertyHandler.map = OrientdbConfigUtil.getParameters();
		CimConstants.defauleSpaceName = "pcopcim";
	}

	@Test
	public void acrossTreeByCopy() throws DataServiceModelRuntimeException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException, DataServiceUserException {
		CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
		CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, "1");

		cimModelCore.setCimDataSpace(cds);

		InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
		InfoObjectDef infoObjectDef = infoObjectDefs.getInfoObjectDef(targetTreeName);
		InformationExplorer ip = cds.getInformationExplorer();
		ExploreParameters ep = new ExploreParameters();
		ep.setType(sourceTreeName);

		FilteringItem filteringItem = new EqualFilteringItem(CimConstants.GeneralProperties.ID, "DATA_MANAGER_CONTENT_TREE_473f6bf1-9c08-4d88-9f4c-3928ad281c69_1572253904393");
		ep.setDefaultFilteringItem(filteringItem);

		List<Fact> factList = ip.discoverInheritFacts(ep);

		if (factList != null && factList.size() > 0) {
			Fact fact = factList.get(0);


			List<Relation> relationList =
					fact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
							RelationDirection.FROM);
			Map<String, Object> insertMap = new HashMap<>();

			Set<String> GeneralPropertySet = new HashSet<>();
			GeneralPropertySet.add(CimConstants.GeneralProperties.ID);
			GeneralPropertySet.add(CimConstants.GeneralProperties.NAME);
			GeneralPropertySet.add(CimConstants.GeneralProperties.COMMENT);
			GeneralPropertySet.add(CimConstants.GeneralProperties.CREATOR_ID);
			GeneralPropertySet.add(CimConstants.GeneralProperties.UPDATOR_ID);
			GeneralPropertySet.add(CimConstants.GeneralProperties.CREATE_TIME);
			GeneralPropertySet.add(CimConstants.GeneralProperties.UPDATE_TIME);


			for (Relation relation : relationList) {
				Relationable toRelationable = relation.getToRelationable();
				if (toRelationable.hasProperty(CimConstants.GeneralProperties.ID) && toRelationable.hasProperty(
						CimConstants.GeneralProperties.NAME)) {


					List<Property> propertyList = toRelationable.getProperties();
					Map<String, Object> basePropertyMap = new HashMap<>();
					Map<String, Object> generalPropertyMap = new HashMap<>();

					for (Property property : propertyList) {
						if (GeneralPropertySet.contains(property.getPropertyName())) {
							basePropertyMap.put(property.getPropertyName(), property.getPropertyValue());

						} else {
							generalPropertyMap.put(property.getPropertyName(), property.getPropertyValue());

						}
					}
					generalPropertyMap.put(CimConstants.TreeNodeBaseInfo.TREE_DEF_ID, targetTreeName);
					InfoObjectValue InfoObjVal = new InfoObjectValue();

					InfoObjVal.setBaseDatasetPropertiesValue(basePropertyMap);
					InfoObjVal.setGeneralDatasetsPropertiesValue(generalPropertyMap);
					InfoObject infoObject = infoObjectDef.newObject(InfoObjVal, false);
				}
			}
		}
	}

	@Test
	public void acrossTreeByCopy2() throws DataServiceModelRuntimeException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException, DataServiceUserException {
		CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
		CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, "1");
		cimModelCore.setCimDataSpace(cds);

		InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
		InfoObjectDef targetInfoObjectDef = infoObjectDefs.getInfoObjectDef(targetTreeName);
		InfoObjectDef sourceInfoObjectDef = infoObjectDefs.getInfoObjectDef(sourceTreeName);
		ExploreParameters ep = new ExploreParameters();

		FilteringItem filteringItem = new EqualFilteringItem(CimConstants.GeneralProperties.ID, "DATA_MANAGER_CONTENT_TREE_64845304-65a7-4819-88c7-c7a6ccbffa2a_1574662954299");
		ep.setDefaultFilteringItem(filteringItem);

		InfoObjectRetrieveResult objects = sourceInfoObjectDef.getObjects(ep);
		InfoObject infoObject = objects.getInfoObjects().get(0);



		recursive(cds,infoObject, targetInfoObjectDef);

		boolean flag = false;

		if(flag){

			recursiveDelete(cds, infoObject);
		}
	}




	//递归
	private InfoObject recursive(CimDataSpace cds,InfoObject infoObject, InfoObjectDef targetInfoObjectDef) throws DataServiceModelRuntimeException, DataServiceUserException, CimDataEngineRuntimeException {
		infoObject.addOrUpdateObjectProperty(CimConstants.TreeNodeBaseInfo.TREE_DEF_ID, targetInfoObjectDef.getObjectTypeName());
		Map<String, Object> baseInfo = infoObject.getInfo();
		InfoObjectValue infoObjectValue = new InfoObjectValue();
		//保存base信息 并清除ID信息
		baseInfo.remove(CimConstants.TreeNodeBaseInfo.ID);
		infoObjectValue.setBaseDatasetPropertiesValue(baseInfo);
		//保存通用信息
		for (Map.Entry<String, Map<String, Object>> unionEntry : infoObject.getObjectPropertiesByDatasets().entrySet()) {
			Map<String, Object> generalInfo = unionEntry.getValue();
			infoObjectValue.setGeneralDatasetsPropertiesValue(generalInfo);
		}
		//第一层
		InfoObject targetInfoObject = targetInfoObjectDef.newObject(infoObjectValue, false);
		permissionService.addDataPermissionByUser(cds,userId,Collections.singletonList(targetInfoObject.getObjectInstanceRID()));
		Fact fact = cds.getFactById(targetInfoObject.getObjectInstanceRID());
		List<InfoObject> allRelatedInfoObjects = infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE, RelationDirection.FROM);
		if(!allRelatedInfoObjects.isEmpty()) {
			for (InfoObject relatedInfoObject : allRelatedInfoObjects) {
				InfoObject targetChildrenInfoObject = recursive(cds, relatedInfoObject, targetInfoObjectDef);
				Fact relatedFact = cds.getFactById(targetChildrenInfoObject.getObjectInstanceRID());
				//递归关系
				relatedFact.addFromRelation(fact, BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
			}
		}
		return targetInfoObject;
	}


	private void recursiveDelete(CimDataSpace cds,InfoObject infoObject) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
		Fact fact = cds.getFactById(infoObject.getObjectInstanceRID());
		//todo 先移除所有关系
		fact.removeAllRelations();
		List<InfoObject> allRelatedInfoObjects = infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE, RelationDirection.FROM);
		if (!allRelatedInfoObjects.isEmpty()) {
			for (InfoObject allRelatedInfoObject : allRelatedInfoObjects) {
				recursiveDelete(cds,allRelatedInfoObject);
			}
		}
	}
}