package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
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
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.RelationTypeBean;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelationshipQueryInput;
import com.glodon.pcop.cimsvc.model.v2.RelatedInstancesBean;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
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
public class RelationshipMappingService {
    private static Logger log = LoggerFactory.getLogger(RelationshipMappingService.class);

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
     * @param queryInput
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<RelationshipMappingVO> getRelationshipsByObjectTypeName(String tenantId, String infoObjectTypeName, ObjectTypeRelationshipQueryInput queryInput) throws CimDataEngineRuntimeException {//NOSONAR
        List<RelationshipMappingVO> mappingVOList = new ArrayList<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            List<RelationshipDef> relationshipDefList;
            switch (queryInput.getRelationDirection()) {
                case FROM:
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.SOURCE);
                    break;
                case TO:
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.TARGET);
                    break;
                default:
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(infoObjectTypeName, RelationshipMappingFeatures.RelationshipRole.BOTH);
                    break;
            }
            if (relationshipDefList != null) {
                for (RelationshipDef relationshipDef : relationshipDefList) {
                    if (StringUtils.isNotBlank(queryInput.getRelationTypeName()) && !queryInput.getRelationTypeName().equals(relationshipDef.getRelationTypeName())) {
                        continue;
                    }
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
    public List<RelatedInstancesBean> getRelatedInstanceByRid(String tenantId, String objectTypeId, String instanceRid, String relationType, ObjectTypeRelatedInstancesQueryInput queryInputBean) throws EntityNotFoundException, DataServiceModelRuntimeException {//NOSONAR
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
            if (relatedObjs != null) {
                for (InfoObject object : relatedObjs) {
                    if (StringUtils.isNotBlank(queryInputBean.getRelatedObjectTypeId()) && !object.getObjectTypeName().equals(queryInputBean.getRelatedObjectTypeId())) {
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
        } else {
            String msg = String.format("Instance of this object type is not difined, objectTypeId=%s, instanceRid=%s", objectTypeId, instanceRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return resultMapList;
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
     * @throws EntityNotFoundException
     */
    public List<ObjectTypeRelatedInstancesQueryOutput> getObjectTypeRelatedInstance(String tenantId, String objectTypeId, String relationTypeName, ObjectTypeRelatedInstancesQueryInput queryInputBean) throws DataServiceModelRuntimeException, EntityNotFoundException {//NOSONAR
        List<InfoObject> infoObjectList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        if (StringUtils.isBlank(queryInputBean.getInstanceRid())) {
            ExploreParameters exploreParameters = new ExploreParameters();
            exploreParameters.setType(objectTypeId);
            InfoObjectRetrieveResult objectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
            infoObjectList = objectRetrieveResult.getInfoObjects();
        } else {
            InfoObject infoObject = infoObjectDef.getObject(queryInputBean.getInstanceRid());
            if (infoObject != null) {
                infoObjectList.add(infoObject);
            } else {
                log.error("instance of {} not found", queryInputBean.getInstanceRid());
            }
        }

        List<ObjectTypeRelatedInstancesQueryOutput> instancesBeanList = new ArrayList<>();
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject infoObject : infoObjectList) {
                ObjectTypeRelatedInstancesQueryOutput objectTypeRelatedInstances = new ObjectTypeRelatedInstancesQueryOutput();
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
                    List<RelatedInstancesBean> relatedInstancesBeans = getRelatedInstanceByRid(tenantId, objectTypeId, infoObject.getObjectInstanceRID(), relationTypeName, queryInputBean);
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
        return instancesBeanList;
    }

}

