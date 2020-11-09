package com.glodon.pcop.cimsvc.service.dataGraph;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefsDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipEdgeData;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipNodeData;
import com.glodon.pcop.cimsvc.model.graph.instance.InstanceNodeData;
import com.glodon.pcop.cimsvc.model.graph.instance.InstancesEdgeData;
import com.glodon.pcop.cimsvc.model.input.GraphObjectTypeInputBean;
import com.glodon.pcop.cimsvc.model.output.RelationShipData;
import com.glodon.pcop.cimsvc.model.output.RelationShipDatasetData;
import com.glodon.pcop.cimsvc.model.vo.InfoObjectTypeDetailVO;
import com.glodon.pcop.cimsvc.model.vo.InstanceRelationsVO;
import com.glodon.pcop.cimsvc.model.vo.RelationInstanceDetailVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2020/6/16 16:26
 */
@Service
public class DataGraphService {


	public RelationShipData relationshipsData(String userId, String tenantId, GraphObjectTypeInputBean conditions) {
		CIMModelCore modelCore = null;


		try {
			modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
			CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			modelCore.setCimDataSpace(cimDataSpace);
			Map<String, RelationshipNodeData> relationshipNodeDataMap = new HashMap<>();
			List<RelationshipEdgeData> relationshipEdgeDataList = new ArrayList<>();
			Map<String, InfoObjectTypeDetailVO> infoObjectTypeDetailVOMap = new HashMap<>();
			List<InfoObjectTypeDetailVO> infoObjectTypeDetailVOList = queryInfoObjectTypeDetails(CimConstants.defauleSpaceName, null, null, null);
			if (infoObjectTypeDetailVOList != null && infoObjectTypeDetailVOList.size() > 0) {
 				for (InfoObjectTypeDetailVO currentInfoObjectTypeDetailVO : infoObjectTypeDetailVOList) {
     					infoObjectTypeDetailVOMap.put(currentInfoObjectTypeDetailVO.getObjectTypeName(), currentInfoObjectTypeDetailVO);
 				}
			}


			List<RelationshipMappingVO> relationshipMappingVOList = queryRelationshipMappingVOList(tenantId, conditions);
 			if (relationshipMappingVOList != null && relationshipMappingVOList.size() > 0) {
				for (RelationshipMappingVO currentRelationshipMappingVO : relationshipMappingVOList) {
					String sourceInfoObjectType = currentRelationshipMappingVO.getSourceInfoObjectType();
					String targetInfoObjectType = currentRelationshipMappingVO.getTargetInfoObjectType();

					setNodeData(infoObjectTypeDetailVOMap, relationshipNodeDataMap, sourceInfoObjectType, modelCore);
					setNodeData(infoObjectTypeDetailVOMap, relationshipNodeDataMap, targetInfoObjectType, modelCore);

					String relationType = currentRelationshipMappingVO.getRelationTypeName();
					String relationshipDesc = currentRelationshipMappingVO.getRelationshipDesc();
					String relationshipId = currentRelationshipMappingVO.getRelationshipId();

					RelationshipEdgeData currentRelationshipEdgeData =
							new RelationshipEdgeData(relationType, relationshipDesc, relationshipId, sourceInfoObjectType, targetInfoObjectType);
					relationshipEdgeDataList.add(currentRelationshipEdgeData);
				}
			}else{
				//查询不到关系的时候，返回本体
				String sourceInfoObjectType = conditions.getSourceInfoObjectType();

				setNodeData(infoObjectTypeDetailVOMap, relationshipNodeDataMap, sourceInfoObjectType, modelCore);


			}
			List<RelationshipNodeData> relationshipNodeDataList = new ArrayList(relationshipNodeDataMap.values());
			RelationShipData relationShipData = new RelationShipData(relationshipNodeDataList, relationshipEdgeDataList);
			return relationShipData;
		} catch (CimDataEngineInfoExploreException e) {
			e.printStackTrace();
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			if (modelCore.getCimDataSpace() != null) {
				modelCore.getCimDataSpace().closeSpace();
			}
		}
		return null;
	}


	public InstanceRelationsVO loadInstanceRelationData(String instanceRID) {
		List<RelationInstanceDetailVO> relationInstanceDetailVOList = getDataInstanceRelationInstanceDetail(instanceRID, 1, 20);
		InstanceRelationsVO instanceRelationsVO = new InstanceRelationsVO();
		instanceRelationsVO.setFocusDataRID(instanceRID);
		List<InstanceNodeData> instanceNodesList = new ArrayList<>();
		List<InstancesEdgeData> instancesEdgesList = new ArrayList<>();


		if (relationInstanceDetailVOList != null && relationInstanceDetailVOList.size() > 0) {
			generateGraphData(instanceNodesList, instancesEdgesList, relationInstanceDetailVOList);
			instanceRelationsVO.setInstanceNodesList(instanceNodesList);
			instanceRelationsVO.setInstancesEdgesList(instancesEdgesList);
			return instanceRelationsVO;
		}


		return instanceRelationsVO;
	}


	private void setNodeData(Map<String, InfoObjectTypeDetailVO> infoObjectTypeDetailVOMap, Map<String, RelationshipNodeData> relationshipNodeDataMap, String infoObjectType, CIMModelCore modelCore) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {

		InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(infoObjectType);
		CimDataSpace ids = modelCore.getCimDataSpace();

		InformationExplorer ie = ids.getInformationExplorer();
		ExploreParameters ep = new ExploreParameters();
		ep.setType(BusinessLogicConstant.DATASETS_RELATIONSHIP_MAPPING_FACT_TYPE_NAME);
		List<Fact> facts = ie.discoverFacts(ep);


		String sourceDataset = BusinessLogicConstant.DATASETS_RELATIONSHIP_FIELDNAME_SOURCETYPE;
		String targetDataset = BusinessLogicConstant.DATASETS_RELATIONSHIP_FIELDNAME_TARGETTYPE;
		String datasetRT = BusinessLogicConstant.DATASETS_RELATIONSHIP_FIELDNAME_RELATIONTYPE;
		String datasetRD = BusinessLogicConstant.DATASETS_RELATIONSHIP_FIELDNAME_RELATIONDESC;
		RelationShipDatasetData relationShipDatasetData = new RelationShipDatasetData();

		if (infoObjectDef != null) {

			List<DatasetDef> datasetDefs = infoObjectDef.getDatasetDefs();

			List<RelationshipNodeData> relationshipNodeDataList = new ArrayList<>();
			List<RelationshipEdgeData> relationshipEdgeDataList = new ArrayList<>();

			for (DatasetDef datasetDef : datasetDefs) {

				String datasetName = datasetDef.getDatasetName();
				RelationshipNodeData datasetRSNodeData = new RelationshipNodeData();
				datasetRSNodeData.setName(datasetName);
				datasetRSNodeData.setDesc(datasetDef.getDatasetDesc());
				datasetRSNodeData.setId(datasetDef.getDatasetRID());
				datasetRSNodeData.setCategory(datasetName);
				relationshipNodeDataList.add(datasetRSNodeData);
				for (Fact fact : facts) {
					String sourceDatasetValue = (String) fact.getProperty(sourceDataset).getPropertyValue();
					String targetDatasetValue = (String) fact.getProperty(targetDataset).getPropertyValue();
					String datasetRTValue = (String) fact.getProperty(datasetRT).getPropertyValue();
					String datasetRDValue = (String) fact.getProperty(datasetRD).getPropertyValue();
					if (datasetName.equals(sourceDatasetValue) || datasetName.equals(targetDatasetValue)) {
						RelationshipEdgeData datasetRSEdgeData = new RelationshipEdgeData();
						datasetRSEdgeData.setSource(sourceDatasetValue);
						datasetRSEdgeData.setTarget(targetDatasetValue);
						datasetRSEdgeData.setId(fact.getId());
						datasetRSEdgeData.setName(datasetRTValue);
						datasetRSEdgeData.setDesc(datasetRDValue);
						relationshipEdgeDataList.add(datasetRSEdgeData);
					}
				}
			}
			relationShipDatasetData.setRelationshipNodeDataList(relationshipNodeDataList);
			relationShipDatasetData.setRelationshipEdgeDataList(relationshipEdgeDataList);
		}

		InfoObjectTypeDetailVO currentInfoObjectTypeDetailVO = infoObjectTypeDetailVOMap.get(infoObjectType);
		if (currentInfoObjectTypeDetailVO != null) {
			RelationshipNodeData newRelationshipNodeData = new RelationshipNodeData(
					currentInfoObjectTypeDetailVO.getObjectTypeName(),
					currentInfoObjectTypeDetailVO.getObjectTypeDesc(),
					currentInfoObjectTypeDetailVO.getObjectTypeName(),
					"notset",
					relationShipDatasetData

			);
			relationshipNodeDataMap.put(infoObjectType, newRelationshipNodeData);
		}
	}

	private List<RelationshipMappingVO> queryRelationshipMappingVOList(String tenantId, GraphObjectTypeInputBean conditions) {
		String sourceInfoObjectType = conditions.getSourceInfoObjectType();
		String targetInfoObjectType = conditions.getTargetInfoObjectType();
		String relationType = conditions.getRelationType();
		String relationshipDesc = conditions.getRelationshipDesc();
		List<RelationshipMappingVO> relationshipMappingVOList = new ArrayList<>();
		CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
		try {
			CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
			targetCIMModelCore.setCimDataSpace(cimDataSpace);
			RelationshipDefs relationshipDefs = targetCIMModelCore.getRelationshipDefs();
			ExploreParameters exploreParameters = new ExploreParameters();
			if (sourceInfoObjectType != null) {
				exploreParameters.setDefaultFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_SOURCETYPE, sourceInfoObjectType));
				if (targetInfoObjectType != null) {
					exploreParameters.addFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_TARGETTYPE, targetInfoObjectType), ExploreParameters.FilteringLogic.AND);
				}
				if (relationType != null) {
					exploreParameters.addFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONTYPE, relationType), ExploreParameters.FilteringLogic.AND);
				}
				if (relationshipDesc != null) {
					exploreParameters.addFilteringItem(new SimilarFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONDESC, relationshipDesc, SimilarFilteringItem.MatchingType.Contain, false), ExploreParameters.FilteringLogic.AND);
				}
			} else {
				if (targetInfoObjectType != null) {
					exploreParameters.setDefaultFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_TARGETTYPE, targetInfoObjectType));
					if (relationType != null) {
						exploreParameters.addFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONTYPE, relationType), ExploreParameters.FilteringLogic.AND);
					}
					if (relationshipDesc != null) {
						exploreParameters.addFilteringItem(new SimilarFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONDESC, relationshipDesc, SimilarFilteringItem.MatchingType.Contain, false), ExploreParameters.FilteringLogic.AND);
					}
				} else {
					if (relationType != null) {
						exploreParameters.setDefaultFilteringItem(new EqualFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONTYPE, relationType));
						if (relationshipDesc != null) {
							exploreParameters.addFilteringItem(new SimilarFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONDESC, relationshipDesc, SimilarFilteringItem.MatchingType.Contain, false), ExploreParameters.FilteringLogic.AND);
						}
					} else {
						if (relationshipDesc != null) {
							exploreParameters.setDefaultFilteringItem(new SimilarFilteringItem(BusinessLogicConstant.INFOOBJECTTYPES_RELATIONSHIP_FIELDNAME_RELATIONDESC, relationshipDesc, SimilarFilteringItem.MatchingType.Contain, false));
						}
					}
				}
			}

			List<RelationshipDef> relationshipDefList = relationshipDefs.queryAllRelationshipDefs(exploreParameters);
			if (relationshipDefList != null) {
				for (RelationshipDef currentRelationshipDef : relationshipDefList) {
					boolean isValidType = true;
					if (tenantId != null && !tenantId.equals(BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME)) {
						isValidType = CommonOperationUtil.isTenantContainsData(tenantId, cimDataSpace.getFactById(currentRelationshipDef.getRelationshipRID()));
					}
					if (isValidType) {
						RelationshipMappingVO currentRelationshipMappingVO = new RelationshipMappingVO();
						currentRelationshipMappingVO.setRelationshipDesc(currentRelationshipDef.getRelationshipDesc());
						currentRelationshipMappingVO.setRelationTypeName(currentRelationshipDef.getRelationTypeName());
						currentRelationshipMappingVO.setSourceInfoObjectType(currentRelationshipDef.getSourceInfoObjectType());
						currentRelationshipMappingVO.setTargetInfoObjectType(currentRelationshipDef.getTargetInfoObjectType());
						currentRelationshipMappingVO.setRelationshipId(currentRelationshipDef.getRelationshipRID());
						relationshipMappingVOList.add(currentRelationshipMappingVO);
					}
				}
			}
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			cimDataSpace.closeSpace();
		}
		return relationshipMappingVOList;
	}

	public static List<InfoObjectTypeDetailVO> queryInfoObjectTypeDetails(String cimSpaceName, String infoObjectTypeName, String infoObjectTypeDesc, String belongedTenant) {
		List<InfoObjectTypeDetailVO> infoObjectTypeDetailVOList = new ArrayList<>();
		CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
		try {
			CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(cimSpaceName, null);
			targetCIMModelCore.setCimDataSpace(cimDataSpace);
			InfoObjectDefsDSImpl infoObjectDefsDSImpl = (InfoObjectDefsDSImpl) targetCIMModelCore.getInfoObjectDefs();

			ExploreParameters exploreParameters = new ExploreParameters();
			if (infoObjectTypeName != null) {
				exploreParameters.setDefaultFilteringItem(new SimilarFilteringItem(
						BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_NAME, infoObjectTypeName, SimilarFilteringItem.MatchingType.Contain, false));
				if (infoObjectTypeDesc != null) {
					exploreParameters.addFilteringItem(new SimilarFilteringItem(
							BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC, infoObjectTypeDesc, SimilarFilteringItem.MatchingType.Contain, false), ExploreParameters.FilteringLogic.AND);
				}
			} else {
				if (infoObjectTypeDesc != null) {
					exploreParameters.setDefaultFilteringItem(new SimilarFilteringItem(
							BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC, infoObjectTypeDesc, SimilarFilteringItem.MatchingType.Contain, false));
				}
			}
			List<InfoObjectDef> matchedInfoObjectDefList = infoObjectDefsDSImpl.queryAllInfoObjectDefsByNoTenantId(exploreParameters);
			for (InfoObjectDef currentInfoObjectDef : matchedInfoObjectDefList) {
				boolean isValidType = true;
				String statusFactRid = ((InfoObjectDefDSImpl) currentInfoObjectDef).getInfoObjectTypeStatusFactRid();
				if (belongedTenant != null) {
					isValidType = CommonOperationUtil.isTenantContainsData(belongedTenant, cimDataSpace.getFactById(statusFactRid));
				}
				if (isValidType) {
					InfoObjectTypeDetailVO infoObjectTypeDetailVO = new InfoObjectTypeDetailVO();
					infoObjectTypeDetailVO.setCreateDateTime(((InfoObjectDefDSImpl) currentInfoObjectDef).getCreateDateTime());
					infoObjectTypeDetailVO.setUpdateDateTime(((InfoObjectDefDSImpl) currentInfoObjectDef).getUpdateDateTime());
					infoObjectTypeDetailVO.setObjectTypeDesc(currentInfoObjectDef.getObjectTypeDesc());
					infoObjectTypeDetailVO.setObjectTypeName(currentInfoObjectDef.getObjectTypeName());
					infoObjectTypeDetailVO.setTenantId(getInstanceBelongedTenantsInfo(cimDataSpace, statusFactRid));
					infoObjectTypeDetailVOList.add(infoObjectTypeDetailVO);
				}
			}
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			cimDataSpace.closeSpace();
		}
		return infoObjectTypeDetailVOList;
	}

	public static String getInstanceBelongedTenantsInfo(CimDataSpace cimDataSpace, String instanceRID) {
		String belongedTenants = "无所属租户信息";
		Fact datasetDefFact = null;
		if (instanceRID != null) {
			try {
				datasetDefFact = cimDataSpace.getFactById(instanceRID);
				List<String> belongedTenantsList = CommonOperationUtil.getBelongedTenants(datasetDefFact);
				if (belongedTenantsList.size() > 0) {
					belongedTenants = formatBelongedTenantsInfo(belongedTenantsList);
				}
			} catch (CimDataEngineRuntimeException e) {
				e.printStackTrace();
			}
		}
		return belongedTenants;
	}

	private static String formatBelongedTenantsInfo(List<String> belongedTenantsList) {
		StringBuffer tenantsInfoString = new StringBuffer();
		for (int i = 0; i < belongedTenantsList.size(); i++) {
			String currentTenant = belongedTenantsList.get(i);
			if (currentTenant.equals(BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME)) {
				tenantsInfoString.append("全局租户共享");
			} else {
				tenantsInfoString.append(currentTenant);
			}
			if (i < belongedTenantsList.size() - 1) {
				tenantsInfoString.append(" , ");
			}
		}
		return tenantsInfoString.toString();
	}


	private static List<RelationInstanceDetailVO> getDataInstanceRelationInstanceDetail(String instanceRID, int startPage, int pageSize) {
		List<RelationInstanceDetailVO> relationInstanceDetailList = new ArrayList<>();
		CimDataSpace cimDataSpace = null;

		try {
			cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, null);
			targetCIMModelCore.setCimDataSpace(cimDataSpace);
			RelationTypeDefs relationTypeDefs = targetCIMModelCore.getRelationTypeDefs();
			InfoObjectDefs infoObjectDefs = targetCIMModelCore.getInfoObjectDefs();
			Map<String, String> def_descMapping = new HashMap<>();
			Measurable targetMeasurable = cimDataSpace.getMeasurableById(instanceRID);
			ExploreParameters exploreParameters = new ExploreParameters();
			exploreParameters.setResultNumber(15);

			if (startPage <= 0) {
				exploreParameters.setStartPage(1);
				exploreParameters.setEndPage(2);
			} else {
				exploreParameters.setStartPage(startPage);
				exploreParameters.setEndPage(startPage + 1);
			}
			if (pageSize <= 0) {
				exploreParameters.setPageSize(20);
			} else {
				exploreParameters.setPageSize(pageSize);
			}
			if (targetMeasurable != null) {
				List<Relation> relationList = null;
				if (targetMeasurable instanceof Fact) {
					relationList = ((Fact) targetMeasurable).getSpecifiedRelations(exploreParameters, RelationDirection.TWO_WAY);
				}
				if (targetMeasurable instanceof Dimension) {
					relationList = ((Dimension) targetMeasurable).getSpecifiedRelations(exploreParameters, RelationDirection.TWO_WAY);
				}
				if (relationList != null) {
					for (Relation currentRelation : relationList) {
						RelationInstanceDetailVO currentRelationInstanceDetailVO = new RelationInstanceDetailVO();
						currentRelationInstanceDetailVO.setRelationInstanceRID(currentRelation.getId());
						currentRelationInstanceDetailVO.setRelationTypeName(currentRelation.getType());

						RelationTypeDef relationTypeDef = relationTypeDefs.getRelationTypeDef(currentRelation.getType());
						if (relationTypeDef != null) {
							currentRelationInstanceDetailVO.setRelationTypeDesc(relationTypeDef.getRelationTypeDesc());
						}

						Relationable fromRelationable = currentRelation.getFromRelationable();
						currentRelationInstanceDetailVO.setFromDataRID(fromRelationable.getId());
						if (fromRelationable instanceof Fact) {
							currentRelationInstanceDetailVO.setFromDataTypeName(((Fact) fromRelationable).getType());
							currentRelationInstanceDetailVO.setFromDataClassify(CimConstants.DataClassify_FACT);
							if (def_descMapping.get(((Fact) fromRelationable).getType()) != null) {
								currentRelationInstanceDetailVO.setFromDataTypeDesc(def_descMapping.get(((Fact) fromRelationable).getType()));
							} else {
								InfoObjectDef infoObjectDef = infoObjectDefs.getInfoObjectDef(((Fact) fromRelationable).getType());
								if (infoObjectDef != null) {
									String defDesc = infoObjectDef.getObjectTypeDesc();
									def_descMapping.put(def_descMapping.get(((Fact) fromRelationable).getType()), defDesc);
									currentRelationInstanceDetailVO.setFromDataTypeDesc(defDesc);
								} else {
									def_descMapping.put(def_descMapping.get(((Fact) fromRelationable).getType()), "-");
									currentRelationInstanceDetailVO.setFromDataTypeDesc("-");
								}
							}
						}
						if (fromRelationable instanceof Dimension) {
							currentRelationInstanceDetailVO.setFromDataTypeName(((Dimension) fromRelationable).getType());
							currentRelationInstanceDetailVO.setFromDataClassify(CimConstants.DataClassify_DIMENSION);
							currentRelationInstanceDetailVO.setFromDataTypeDesc("-");
						}

						Relationable toRelationable = currentRelation.getToRelationable();
						currentRelationInstanceDetailVO.setToDataRID(toRelationable.getId());
						if (toRelationable instanceof Fact) {
							currentRelationInstanceDetailVO.setToDataTypeName(((Fact) toRelationable).getType());
							currentRelationInstanceDetailVO.setToDataClassify(CimConstants.DataClassify_FACT);
							if (def_descMapping.get(((Fact) toRelationable).getType()) != null) {
								currentRelationInstanceDetailVO.setToDataTypeDesc(def_descMapping.get(((Fact) toRelationable).getType()));
							} else {
								InfoObjectDef infoObjectDef = infoObjectDefs.getInfoObjectDef(((Fact) toRelationable).getType());
								if (infoObjectDef != null) {
									String defDesc = infoObjectDef.getObjectTypeDesc();
									def_descMapping.put(def_descMapping.get(((Fact) toRelationable).getType()), defDesc);
									currentRelationInstanceDetailVO.setToDataTypeDesc(defDesc);
								} else {
									def_descMapping.put(def_descMapping.get(((Fact) toRelationable).getType()), "-");
									currentRelationInstanceDetailVO.setToDataTypeDesc("-");
								}
							}
						}
						if (toRelationable instanceof Dimension) {
							currentRelationInstanceDetailVO.setToDataTypeName(((Dimension) toRelationable).getType());
							currentRelationInstanceDetailVO.setToDataClassify(CimConstants.DataClassify_DIMENSION);
							currentRelationInstanceDetailVO.setToDataTypeDesc("-");
						}
						relationInstanceDetailList.add(currentRelationInstanceDetailVO);
					}
				}
			}
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			if (cimDataSpace != null) {
				cimDataSpace.closeSpace();
			}
		}
		return relationInstanceDetailList;
	}

	private void generateGraphData(List<InstanceNodeData> instanceNodesList, List<InstancesEdgeData> instancesEdgesList, List<RelationInstanceDetailVO> relationInstanceDetailVOList) {
		List<String> existNodeIdList = new ArrayList<>();

		for (RelationInstanceDetailVO currentRelationInstanceDetailVO : relationInstanceDetailVOList) {
			currentRelationInstanceDetailVO.getFromDataTypeName();
			currentRelationInstanceDetailVO.getFromDataRID();
			currentRelationInstanceDetailVO.getFromDataClassify();

			InstanceNodeData sourceInstanceNodeData = new InstanceNodeData(
					currentRelationInstanceDetailVO.getFromDataTypeName(),
					currentRelationInstanceDetailVO.getFromDataTypeDesc(),
					currentRelationInstanceDetailVO.getFromDataRID(),
					currentRelationInstanceDetailVO.getFromDataClassify()
			);
			if (!existNodeIdList.contains(currentRelationInstanceDetailVO.getFromDataRID())) {
				instanceNodesList.add(sourceInstanceNodeData);
				existNodeIdList.add(currentRelationInstanceDetailVO.getFromDataRID());
			}

			InstanceNodeData targetInstanceNodeData = new InstanceNodeData(
					currentRelationInstanceDetailVO.getToDataTypeName(),
					currentRelationInstanceDetailVO.getToDataTypeDesc(),
					currentRelationInstanceDetailVO.getToDataRID(),
					currentRelationInstanceDetailVO.getToDataClassify()
			);
			if (!existNodeIdList.contains(currentRelationInstanceDetailVO.getToDataRID())) {
				instanceNodesList.add(targetInstanceNodeData);
				existNodeIdList.add(currentRelationInstanceDetailVO.getToDataRID());
			}

			InstancesEdgeData instancesEdgeData = new InstancesEdgeData(
					currentRelationInstanceDetailVO.getRelationTypeName(),
					currentRelationInstanceDetailVO.getRelationTypeDesc(),
					currentRelationInstanceDetailVO.getRelationInstanceRID(),
					currentRelationInstanceDetailVO.getFromDataRID(),
					currentRelationInstanceDetailVO.getToDataRID()
			);
			instancesEdgesList.add(instancesEdgeData);
		}
	}


}
