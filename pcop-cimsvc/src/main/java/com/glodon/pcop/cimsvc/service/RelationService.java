package com.glodon.pcop.cimsvc.service;

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
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationshipDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationshipDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.ObjectTypeRelatedInstancesBean;
import com.glodon.pcop.cimsvc.model.RelatedInstancesBean;
import com.glodon.pcop.cimsvc.model.RelatedIntsanceQueryInputBean;
import com.glodon.pcop.cimsvc.model.RelationTypeBean;
import com.glodon.pcop.cimsvc.model.RelationshipQueryInputBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanjk
 * @description 关系相关
 * @date 2018/9/26 10:40
 */
@Service
public class RelationService {
    private static Logger log = LoggerFactory.getLogger(RelationService.class);

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
                    RelationTypeBean relationTypeBean = new RelationTypeBean(relationTypeDef.getRelationTypeName(), relationTypeDef.getRelationTypeDesc(), relationTypeDef.isDisabled());
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
     * @param relationshipMappingVO
     * @return
     */
    public RelationshipMappingVO addRelationshipDef(String tenantId, RelationshipMappingVO relationshipMappingVO) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            return relationshipDefs.addRelationshipDef(relationshipMappingVO.getSourceInfoObjectType(), relationshipMappingVO.getTargetInfoObjectType(), relationshipMappingVO.getRelationTypeName(), relationshipMappingVO.getRelationshipDesc(), relationshipMappingVO.getLinkLogic());
        } else {
            return null;
        }
    }

    /**
     * 更新对象关系定义
     *
     * @param tenantId
     * @param relationshipMappingVO
     * @return
     */
    public RelationshipMappingVO updateRelationshipDef(String tenantId, String relationshipId, RelationshipMappingVO relationshipMappingVO) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            try {
                return relationshipDefs.updateRelationshipDef(relationshipId, relationshipMappingVO.getRelationshipDesc(), relationshipMappingVO.getLinkLogic());
            } catch (DataServiceModelRuntimeException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 删除对象关系定义
     *
     * @param tenantId
     * @param relationshipId
     * @return
     */
    public boolean deleteRelationshipDef(String tenantId, String relationshipId) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            try {
                return relationshipDefs.removeRelationshipDef(relationshipId);
            } catch (DataServiceModelRuntimeException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取与指定对象类型相关的所有对象关系
     *
     * @param infoObjectTypeName
     * @param relationRole
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<RelationshipMappingVO> getRelationshipsByObjectTypeName(String tenantId, String infoObjectTypeName, String relationRole) throws CimDataEngineRuntimeException {
        List<RelationshipMappingVO> mappingVOList = new ArrayList<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            List<RelationshipDef> relationshipDefList;
            switch (relationRole) {
                case "SOURCE":
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.SOURCE);
                    break;
                case "TARGET":
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.TARGET);
                    break;
                default:
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.BOTH);
                    break;
            }
            if (relationshipDefList != null) {
                for (RelationshipDef relationshipDef : relationshipDefList) {
                    RelationshipMappingVO mappingVO = RelationshipMappingFeatures.getRelationshipMappingById(CimConstants.defauleSpaceName, relationshipDef.getRelationshipRID());
                    if (mappingVO != null) {
                        mappingVOList.add(mappingVO);
                    }
                }
            }
        }
        return mappingVOList;
    }

    /**
     * 查询关联实例，指定类型，TO方向
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceRid
     * @param queryInputBean
     * @return
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public List<RelatedInstancesBean> getRelatedInstanceByRid(String tenantId, String infoObjectTypeName, String instanceRid, RelatedIntsanceQueryInputBean queryInputBean) throws EntityNotFoundException, DataServiceModelRuntimeException {
        List<RelatedInstancesBean> resultMapList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceRid);
        if (infoObject != null) {
            ExploreParameters ep = new ExploreParameters();
            ep.setStartPage(queryInputBean.getStartPage());
            ep.setEndPage(queryInputBean.getEndPage());
            ep.setPageSize(queryInputBean.getPageSize());
            ep.addRelatedRelationType(queryInputBean.getRelationTypeName());
//            List<InfoObject> relatedObjs = infoObject.getRelatedInfoObjects(ep, RelationDirection.FROM);
            List<InfoObject> relatedObjs = infoObject.getRelatedInfoObjects(ep, RelationDirection.TO);
            if (relatedObjs != null) {
                for (InfoObject object : relatedObjs) {
                    Map<String, Map<String, Object>> resultMapTemp = object.getObjectPropertiesByDatasets();
                    if (resultMapTemp == null) {
                        resultMapTemp = new HashMap<>();
                    }
                    resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
                    if (resultMapTemp.size() > 0) {
                        RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(), object.getObjectInstanceRID(), resultMapTemp);
                        resultMapList.add(instancesBean);
                    }
                }
            }
        } else {
            String msg = String.format("Instance of this object type is not difined, ObjectTypeName=%s, RID=%s", infoObjectTypeName, instanceRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return resultMapList;
    }

    /**
     * 关系方向，关系类型，关联对象的类型
     *
     * @param tenantId
     * @param objectTypeId
     * @param instanceRid
     * @param relationType
     * @param queryInputBean
     * @return
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public List<RelatedInstancesBean> getRelatedInstanceByRid(String tenantId, String objectTypeId, String instanceRid, String relationType, RelationshipQueryInputBean queryInputBean) throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
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
                        if (StringUtils.isNotBlank(queryInputBean.getSrcObjectTypeId()) && !object.getObjectTypeName().equals(queryInputBean.getSrcObjectTypeId())) {
                            continue;
                        }
                        Map<String, Map<String, Object>> resultMapTemp = object.getObjectPropertiesByDatasets();
                        if (resultMapTemp == null) {
                            resultMapTemp = new HashMap<>();
                        }
                        resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
                        if (resultMapTemp.size() > 0) {
                            RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(), object.getObjectInstanceRID(), resultMapTemp);
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
            String msg = String.format("Instance of this object type is not difined, objectTypeId=%s, instanceRid=%s", objectTypeId, instanceRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return resultMapList;
    }

    public List<RelatedInstancesBean> getRelatedInstanceBaseInfoByRid(String tenantId, String objectTypeId, String instanceRid, String relationType, RelationshipQueryInputBean queryInputBean) throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
        List<RelatedInstancesBean> resultMapList = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            // InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
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
                // CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                if (relatedObjs != null) {
                    for (InfoObject object : relatedObjs) {
                        ((InfoObjectDSImpl) object).setCimDataSpace(cds);
                        if (StringUtils.isNotBlank(queryInputBean.getSrcObjectTypeId()) && !object.getObjectTypeName().equals(queryInputBean.getSrcObjectTypeId())) {
                            continue;
                        }
                        Map<String, Map<String, Object>> resultMapTemp = new HashMap<>();
                        resultMapTemp.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, object.getInfo());
                        if (resultMapTemp.size() > 0) {
                            RelatedInstancesBean instancesBean = new RelatedInstancesBean(object.getObjectTypeName(), object.getObjectInstanceRID(), resultMapTemp);
                            resultMapList.add(instancesBean);
                        }
                    }
                }
            } else {
                String msg = String.format("Instance of this object type is not difined, objectTypeId=%s, instanceRid=%s", objectTypeId, instanceRid);
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

    /**
     * 查询指定对象类型所有实例的关联实例，关系类型，关系方向（可选），关联对象类型（可选）
     *
     * @param tenantId
     * @param objectTypeId
     * @param relationType
     * @param queryInputBean
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public List<ObjectTypeRelatedInstancesBean> getObjectTypeRelatedInstance(String tenantId, String objectTypeId, String relationType, RelationshipQueryInputBean queryInputBean) throws DataServiceModelRuntimeException, EntityNotFoundException {
        List<ObjectTypeRelatedInstancesBean> instancesBeanList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(objectTypeId);
        InfoObjectRetrieveResult objectRetrieveResult = infoObjectDef.getObjects(exploreParameters);

        if (objectRetrieveResult != null && objectRetrieveResult.getInfoObjects().size() > 0) {
            for (InfoObject infoObject : objectRetrieveResult.getInfoObjects()) {
                ObjectTypeRelatedInstancesBean typeRelatedInstancesBean = new ObjectTypeRelatedInstancesBean();
                typeRelatedInstancesBean.setInfoObjectId(infoObject.getObjectInstanceRID());
                typeRelatedInstancesBean.setObjectTypeName(infoObject.getObjectTypeName());
                Map<String, Map<String, Object>> valuesByDataSet = infoObject.getObjectPropertiesByDatasets();
                if (valuesByDataSet == null) {
                    valuesByDataSet = new HashMap<>();
                }
                valuesByDataSet.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
                // typeRelatedInstancesBean.setDataSetValue(infoObject.getObjectPropertiesByDatasets());
                typeRelatedInstancesBean.setDataSetValue(valuesByDataSet);
                try {
                    List<RelatedInstancesBean> relatedInstancesBeans = getRelatedInstanceByRid(tenantId, objectTypeId, infoObject.getObjectInstanceRID(), relationType, queryInputBean);
                    if (relatedInstancesBeans != null) {
                        typeRelatedInstancesBean.setInstanceCount(relatedInstancesBeans.size());
                    } else {
                        typeRelatedInstancesBean.setInstanceCount(0);
                    }
                    typeRelatedInstancesBean.setRelatedInstances(relatedInstancesBeans);
                } catch (Exception e) {
                    log.error("query related instance failed", e);
                }
                instancesBeanList.add(typeRelatedInstancesBean);
            }
        }
        return instancesBeanList;
    }


}

