package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.RelationshipMappingFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.RelationTypeBean;
import com.glodon.pcop.cimsvc.model.RelationshipQueryInputBean;
import com.glodon.pcop.cimsvc.model.transverter.RelationshipTsr;
import com.glodon.pcop.cimsvc.model.v2.BasePageableQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.BaseQueryInputBean;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelationshipQueryInput;
import com.glodon.pcop.cimsvc.model.v2.RelatedInstancesBean;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.glodon.pcop.cimsvc.service.v2.engine.RelationshipMappingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RelationshipsService {
	private static Logger log = LoggerFactory.getLogger(RelationshipsService.class);

	@Autowired
	RelationshipMappingService relationshipMappingService;

	/**
	 * 获取所有的已定义关系类型
	 *
	 * @param tenantId
	 * @return
	 */
	public List<RelationTypeBean> getAllRelationTypes(String tenantId) {
		List<RelationTypeBean> relationTypeBeanList = new ArrayList<>();
		CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
		RelationTypeDefs relationTypeDefs = cimModelCore.getRelationTypeDefs();
		if (relationTypeDefs != null) {
			List<RelationTypeDef> relationTypeDefList = relationTypeDefs.getRelationTypeDefs();
			if (relationTypeDefList != null && relationTypeDefList.size() > 0) {
				for (RelationTypeDef relationTypeDef : relationTypeDefList) {
					RelationTypeBean relationTypeBean = new RelationTypeBean(relationTypeDef.getRelationTypeName(),
							relationTypeDef.getRelationTypeDesc(), relationTypeDef.isDisabled());
					relationTypeBeanList.add(relationTypeBean);
				}
			}
		}
		return relationTypeBeanList;
	}

	public List<RelationTypeBean> getAllRelationTypes(String tenantId, CimDataSpace cds) {
		List<RelationTypeBean> relationTypeBeanList = new ArrayList<>();
		CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
		cimModelCore.setCimDataSpace(cds);
		RelationTypeDefs relationTypeDefs = cimModelCore.getRelationTypeDefs();
		if (relationTypeDefs != null) {
			List<RelationTypeDef> relationTypeDefList = relationTypeDefs.getRelationTypeDefs();
			if (relationTypeDefList != null && relationTypeDefList.size() > 0) {
				for (RelationTypeDef relationTypeDef : relationTypeDefList) {
					RelationTypeBean relationTypeBean = new RelationTypeBean(relationTypeDef.getRelationTypeName(),
							relationTypeDef.getRelationTypeDesc(), relationTypeDef.isDisabled());
					relationTypeBeanList.add(relationTypeBean);
				}
			}
		}
		return relationTypeBeanList;
	}

	/**
	 * 新增对象关系定义
	 *
	 * @param tenantId
	 * @param relationshipEntity
	 * @return
	 */
	public RelationshipEntity addRelationshipDef(String tenantId, RelationshipEntity relationshipEntity) {
		if (relationshipEntity == null) {
			log.error("relationship entity is null");
			return null;
		}
		RelationshipMappingVO relationshipMappingVO = RelationshipTsr.entityToVo(relationshipEntity);
		relationshipMappingVO = relationshipMappingService.addRelationshipDef(tenantId, relationshipMappingVO);
		return RelationshipTsr.voToEntity(relationshipMappingVO);
	}

	/**
	 * 更新对象关系定义
	 *
	 * @param tenantId
	 * @param relationshipEntity
	 * @return
	 */
	public RelationshipEntity updateRelationshipDef(String tenantId, String relationshipId,
													RelationshipEntity relationshipEntity) {
		if (relationshipEntity == null) {
			log.error("relationship entity is null");
			return null;
		}
		RelationshipMappingVO relationshipMappingVO = RelationshipTsr.entityToVo(relationshipEntity);
		relationshipMappingVO = relationshipMappingService.updateRelationshipDef(tenantId, relationshipId,
				relationshipMappingVO);
		return RelationshipTsr.voToEntity(relationshipMappingVO);
	}

	/**
	 * 删除对象关系定义
	 *
	 * @param tenantId
	 * @param relationshipId
	 * @return
	 */
	public boolean deleteRelationshipDef(String tenantId, String relationshipId) {
		return relationshipMappingService.deleteRelationshipDef(tenantId, relationshipId);
	}

	/**
	 * 查询对象关系定义
	 *
	 * @param tenantId
	 * @param relationshipId
	 * @return
	 */
	public RelationshipMappingVO getRelationshipDef(String tenantId, String relationshipId)
			throws CimDataEngineRuntimeException {
		return RelationshipMappingFeatures.getRelationshipMappingById(CimConstants.defauleSpaceName, relationshipId);
	}

	/**
	 * 获取与指定对象类型相关的所有对象关系
	 *
	 * @param objectTypeId
	 * @param queryInput
	 * @return
	 * @throws CimDataEngineRuntimeException
	 */
	public List<RelationshipEntity> getRelationshipsByObjectTypeName(String tenantId, String objectTypeId,
																	 ObjectTypeRelationshipQueryInput queryInput)
			throws CimDataEngineRuntimeException {

		List<RelationshipMappingVO> relationshipMappingVOS =
				relationshipMappingService.getRelationshipsByObjectTypeName(tenantId, objectTypeId, queryInput);
		if (relationshipMappingVOS == null) {
			return null;
		}
		List<RelationshipEntity> relationshipEntities = new ArrayList<>();
		for (RelationshipMappingVO mappingVO : relationshipMappingVOS) {
			relationshipEntities.add(RelationshipTsr.voToEntity(mappingVO));
		}

		return relationshipEntities;
	}

	/**
	 * 查询指定对象类型所有实例的关联实例，关系类型，关系方向（可选），关联对象类型（可选）
	 *
	 * @param tenantId
	 * @param objectTypeId
	 * @param relationTypeName
	 * @param queryInputBean
	 * @return
	 * @throws DataServiceModelRuntimeException
	 */
	public BasePageableQueryOutput<ObjectTypeRelatedInstancesQueryOutput> getRelatedInstanceByObjectType(
			String tenantId, String objectTypeId, String relationTypeName,
			ObjectTypeRelatedInstancesQueryInput queryInputBean) throws DataServiceModelRuntimeException {//NOSONAR
		BasePageableQueryOutput<ObjectTypeRelatedInstancesQueryOutput> result = new BasePageableQueryOutput<>();
		result.setTotalCount(0L);
		CimDataSpace cds = null;
		try {
			cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			List<InfoObject> infoObjectList = new ArrayList<>();
			CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
			modelCore.setCimDataSpace(cds);
			InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
			if (StringUtils.isBlank(queryInputBean.getInstanceRid()) && StringUtils.isBlank(queryInputBean.getId())) {
				ExploreParameters exploreParameters = new ExploreParameters();
				exploreParameters.setType(objectTypeId);
				if (queryInputBean.getStartPage() > 0 && queryInputBean.getEndPage() > 0 && queryInputBean.getEndPage() > queryInputBean.getStartPage()) {
					exploreParameters.setStartPage(queryInputBean.getStartPage());
					exploreParameters.setEndPage(queryInputBean.getEndPage());
				} else {
					exploreParameters.setStartPage(1);
					exploreParameters.setEndPage(2);
				}
				if (exploreParameters.getPageSize() > 0) {
					exploreParameters.setPageSize(queryInputBean.getPageSize());
				} else {
					exploreParameters.setPageSize(10);
				}
				InfoObjectRetrieveResult objectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
				infoObjectList = objectRetrieveResult.getInfoObjects();
				exploreParameters.setStartPage(1);
				exploreParameters.setEndPage(2);
				exploreParameters.setPageSize(Integer.MAX_VALUE);
				InfoObjectRetrieveResult allObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
				long totalSize = allObjectRetrieveResult.getOperationStatistics().getResultDataNumber();
				result.setTotalCount(totalSize);
			} else if (StringUtils.isNotBlank(queryInputBean.getInstanceRid()) && StringUtils.isBlank(queryInputBean.getId())) {
				InfoObject infoObject = infoObjectDef.getObject(queryInputBean.getInstanceRid());
				if (infoObject != null) {
					infoObjectList.add(infoObject);
					result.setTotalCount(1L);
				} else {
					log.error("instance of {} not found", queryInputBean.getInstanceRid());
				}
			} else if (StringUtils.isBlank(queryInputBean.getInstanceRid()) && StringUtils.isNotBlank(queryInputBean.getId())) {
				InfoObject infoObject = infoObjectDef.getObjectByID(queryInputBean.getId());
				if (infoObject != null) {
					infoObjectList.add(infoObject);
					result.setTotalCount(1L);
				} else {
					log.error("instance of {} not found", queryInputBean.getId());
				}
			}

			List<ObjectTypeRelatedInstancesQueryOutput> instancesBeanList = new ArrayList<>();
			if (infoObjectList != null && infoObjectList.size() > 0) {
				for (InfoObject infoObject : infoObjectList) {
					ObjectTypeRelatedInstancesQueryOutput objectTypeRelatedInstances =
							new ObjectTypeRelatedInstancesQueryOutput();
					objectTypeRelatedInstances.setInstanceRid(infoObject.getObjectInstanceRID());
					objectTypeRelatedInstances.setObjectTypeId(infoObject.getObjectTypeName());
					Map<String, Map<String, Object>> valuesByDataSet = infoObject.getObjectPropertiesByDatasets();
					// objectTypeRelatedInstances.setInstanceData(infoObject.getObjectPropertiesByDatasets());
					if (valuesByDataSet == null) {
						valuesByDataSet = new HashMap<>();
					}
					valuesByDataSet.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
					objectTypeRelatedInstances.setInstanceData(valuesByDataSet);
					try {
						List<RelatedInstancesBean> relatedInstancesBeans = getAllRelatedInstance(infoObject,
								relationTypeName, queryInputBean);
						if (relatedInstancesBeans != null) {
							objectTypeRelatedInstances.setInstanceCount(relatedInstancesBeans.size());
						} else {
							objectTypeRelatedInstances.setInstanceCount(0);
						}
						objectTypeRelatedInstances.setRelatedInstances(relatedInstancesBeans);
					} catch (Exception e) {
						log.error("query related instance failed", e);
					}
					instancesBeanList.add(objectTypeRelatedInstances);
				}
			}
			result.setInstances(instancesBeanList);
		} finally {
			if (cds != null) {
				cds.closeSpace();
			}
		}

		return result;
	}

	public List<RelatedInstancesBean> getRelatedInstanceBaseInfoByRid(String tenantId, String objectTypeId,
																	  String instanceRid, String relationType,
																	  RelationshipQueryInputBean queryInputBean)
			throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
		List<RelatedInstancesBean> resultMapList = new ArrayList<>();
		InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
		InfoObject infoObject = infoObjectDef.getObject(instanceRid);
		if (infoObject != null) {
			ExploreParameters ep = new ExploreParameters();
			ep.setStartPage(queryInputBean.getStartPage());
			ep.setEndPage(queryInputBean.getEndPage());
			ep.setPageSize(queryInputBean.getPageSize());
			ep.addRelatedRelationType(relationType);
			RelationDirection dirc = RelationDirection.TO;
			if (queryInputBean.getRelationDirection() != null) {
				dirc = queryInputBean.getRelationDirection();
			}
			List<InfoObject> relatedObjs = infoObject.getRelatedInfoObjects(ep, dirc);
			// CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			CimDataSpace cds = null;
			try {
				cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
				if (relatedObjs != null) {
					for (InfoObject object : relatedObjs) {
						((InfoObjectDSImpl) object).setCimDataSpace(cds);
						if (StringUtils.isNotBlank(
								queryInputBean.getSrcObjectTypeId()) && !object.getObjectTypeName().equals(
								queryInputBean.getSrcObjectTypeId())) {
							continue;
						}
						Map<String, Map<String, Object>> resultMapTemp = new HashMap<>();
						resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
						if (resultMapTemp.size() > 0) {
							RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(),
									object.getObjectInstanceRID(), resultMapTemp);
							resultMapList.add(instancesBean);
						}
					}
				}
			} finally {
				if (cds != null) {
					cds.closeSpace();
				}
			}
		} else {
			String msg = String.format("Instance of this object type is not difined, objectTypeId=%s, instanceRid=%s"
					, objectTypeId, instanceRid);
			log.error(msg);
			throw new EntityNotFoundException(msg);
		}
		return resultMapList;
	}

	public List<ObjectTypeRelatedInstancesQueryOutput> getRelatedInstance(String tenantId, String objectTypeId,
																		  String relationshipId,
																		  BaseQueryInputBean queryInputBean) {
		List<ObjectTypeRelatedInstancesQueryOutput> objectTypeRelatedInstances = new ArrayList<>();
		return objectTypeRelatedInstances;
	}

	public List<RelatedInstancesBean> getAllRelatedInstance(InfoObject infoObject, String relationType,
															ObjectTypeRelatedInstancesQueryInput queryInputBean)
			throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
		List<RelatedInstancesBean> resultMapList = new ArrayList<>();
		if (infoObject != null) {
			ExploreParameters ep = new ExploreParameters();
			ep.setResultNumber(Integer.MAX_VALUE);
			ep.addRelatedRelationType(relationType);
			RelationDirection dirc = RelationDirection.TO;
			if (queryInputBean.getRelationDirection() != null) {
				dirc = queryInputBean.getRelationDirection();
			}

			List<InfoObject> relatedObjs = infoObject.getRelatedInfoObjects(ep, dirc);
			if (relatedObjs != null) {
				for (InfoObject object : relatedObjs) {
					if (StringUtils.isNotBlank(
							queryInputBean.getRelatedObjectTypeId()) && !object.getObjectTypeName().equals(
							queryInputBean.getRelatedObjectTypeId())) {
						continue;
					}
					Map<String, Map<String, Object>> resultMapTemp = object.getObjectPropertiesByDatasets();
					if (resultMapTemp == null) {
						resultMapTemp = new HashMap<>();
					}
					resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
					if (resultMapTemp.size() > 0) {
						RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(),
								object.getObjectInstanceRID(), resultMapTemp);
						resultMapList.add(instancesBean);
					}
				}
			} else {
				log.info("relate infoobject size: [0]");
			}
		} else {
			String msg = String.format("InfoObject is null");
			log.error(msg);
			throw new EntityNotFoundException(msg);
		}
		return resultMapList;
	}

	public List<RelatedInstancesBean> getRelatedInstanceGeneralInfoByRid(String tenantId, String objectTypeId,
																		 String instanceRid, String relationType,
																		 RelationshipQueryInputBean queryInputBean)
			throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
		List<RelatedInstancesBean> resultMapList = new ArrayList<>();
		CimDataSpace cds = null;
		try {
			cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
			CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
			modelCore.setCimDataSpace(cds);
			InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
			InfoObject infoObject = infoObjectDef.getObject(instanceRid);
			if (infoObject != null) {
				ExploreParameters ep = new ExploreParameters();
				ep.setStartPage(queryInputBean.getStartPage());
				ep.setEndPage(queryInputBean.getEndPage());
				ep.setPageSize(queryInputBean.getPageSize());
				ep.addRelatedRelationType(relationType);
				RelationDirection dirc = RelationDirection.TO;
				if (queryInputBean.getRelationDirection() != null) {
					dirc = queryInputBean.getRelationDirection();
				}
				List<InfoObject> relatedObjs = infoObject.getRelatedInfoObjects(ep, dirc);
				if (relatedObjs != null) {
					for (InfoObject object : relatedObjs) {
						// ((InfoObjectDSImpl) object).setCimDataSpace(cds);
						if (StringUtils.isNotBlank(
								queryInputBean.getSrcObjectTypeId()) && !object.getObjectTypeName().equals(
								queryInputBean.getSrcObjectTypeId())) {
							continue;
						}
						Map<String, Map<String, Object>> resultMapTemp = object.getObjectPropertiesByDatasets();
						if (resultMapTemp == null) {
							resultMapTemp = new HashMap<>();
						}
						resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
						if (resultMapTemp.size() > 0) {
							RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(),
									object.getObjectInstanceRID(), resultMapTemp);
							resultMapList.add(instancesBean);
						}
					}
				}
			} else {
				String msg = String.format("Instance of this object type is not difined, objectTypeId=%s, " +
						"instanceRid=%s", objectTypeId, instanceRid);
				log.error(msg);
				throw new EntityNotFoundException(msg);
			}
		} finally {
			if (cds != null) {
				cds.closeSpace();
			}
		}
		return resultMapList;
	}

}

