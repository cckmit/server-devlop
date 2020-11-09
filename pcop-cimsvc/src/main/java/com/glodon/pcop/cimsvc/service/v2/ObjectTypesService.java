package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.check.object.CheckAndAddDataSetInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.AddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.AddObjectTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdateObjectTypeOutputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.UniversalDimensionAttachInfo;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.DouplicateObjectIdException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.transverter.DataSetTsr;
import com.glodon.pcop.cimsvc.model.transverter.ObjectTypeTsr;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelationshipQueryInput;
import com.glodon.pcop.cimsvc.service.v2.engine.DataSetDefService;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ObjectTypesService {
    private static Logger log = LoggerFactory.getLogger(ObjectTypesService.class);

    @Autowired
    private InfoObjectTypeDefService infoObjectTypeDefService;

    @Autowired
    private RelationshipsService relationshipsService;

    @Autowired
    private DataSetDefService dataSetDefService;


    /**
     * 对象模型定义
     *
     * @param tenantId
     * @param objectTypeId
     * @param isIncludedDataSet
     * @param isIncludedProperty
     * @return
     */
    public ObjectTypeEntity getObjectType(String tenantId, String objectTypeId, boolean isIncludedDataSet,
                                          boolean isIncludedProperty, String dataSetType) {
        InfoObjectTypeVO infoObjectTypeVO = infoObjectTypeDefService.getObjectTypeDef(tenantId, objectTypeId,
                isIncludedDataSet, isIncludedProperty, dataSetType);
        if (infoObjectTypeVO != null) {
            return ObjectTypeTsr.voToEntity(infoObjectTypeVO, isIncludedDataSet, isIncludedProperty);
        } else {
            return null;
        }
    }

    /**
     * 新增对象模型
     *
     * @param tenantId
     * @param objectType
     * @return
     * @throws DataServiceModelRuntimeException
     */
    @Deprecated
    public ObjectTypeEntity addObjectType(String tenantId, ObjectTypeEntity objectType)
            throws DataServiceModelRuntimeException {
        if (objectType == null) {
            log.error("object type is null");
            return null;
        }
        InfoObjectTypeVO infoObjectTypeVO = ObjectTypeTsr.entityToVo(objectType, false, false);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            String objectTypeId = infoObjectTypeDefService.addObjectTypeDef(cds, tenantId, infoObjectTypeVO);
            if (StringUtils.isBlank(objectTypeId)) {
                log.error("object type add failed");
                return null;
            }
            InfoObjectTypeVO resultInfoObjectTypeVO = infoObjectTypeDefService.getObjectTypeDef(cds, tenantId,
                    objectTypeId, false, false);
            if (resultInfoObjectTypeVO != null) {
                ObjectTypeEntity entity = ObjectTypeTsr.voToEntity(resultInfoObjectTypeVO, false, false);
                return entity;
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }

    public AddObjectTypeOutputBean addObjectType(String tenantId, AddObjectTypeInputBean inputBean)
            throws DataServiceModelRuntimeException, DouplicateObjectIdException {
        Assert.notNull(inputBean, "object type is null");
        InfoObjectTypeVO infoObjectTypeVO = ObjectTypeTsr.addInputBeanToVo(inputBean);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            if (StringUtils.isNotBlank(inputBean.getName()) && cds.hasInheritFactType(inputBean.getName())) {
                throw new DouplicateObjectIdException("object type is already exists");
            }

            String objectTypeId = infoObjectTypeDefService.addObjectTypeDef(cds, tenantId, infoObjectTypeVO);
            if (StringUtils.isBlank(objectTypeId)) {
                log.error("object type add failed");
                return null;
            }
            InfoObjectTypeVO resultInfoObjectTypeVO = infoObjectTypeDefService.getObjectTypeDef(cds, tenantId,
                    objectTypeId, false, false);
            if (resultInfoObjectTypeVO != null) {
                AddObjectTypeOutputBean outputBean = ObjectTypeTsr.voToAddOutputBean(resultInfoObjectTypeVO);
                return outputBean;
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }

    /**
     * 更新对象模型
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectType
     * @return
     * @throws DataServiceModelRuntimeException
     */
    @Deprecated
    public ObjectTypeEntity updateObjectType(String tenantId, String objectTypeId, ObjectTypeEntity objectType)
            throws DataServiceModelRuntimeException {
        if (objectType == null) {
            log.error("input is null");
            return null;
        }

        InfoObjectTypeVO infoObjectTypeVO = ObjectTypeTsr.entityToVo(objectType, false, false);
        infoObjectTypeVO = infoObjectTypeDefService.updateObjectTypeDef(tenantId, objectTypeId, infoObjectTypeVO);
        if (infoObjectTypeVO == null) {
            log.error("update object type failed");
            return null;
        }

        return ObjectTypeTsr.voToEntity(infoObjectTypeVO, false, false);
    }

    /**
     * 更新对象模型名称及其关联的行业分类
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectType
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public UpdateObjectTypeOutputBean updateObjectType(String tenantId, String objectTypeId,
                                                       UpdateObjectTypeInputBean objectType)
            throws DataServiceModelRuntimeException {
        if (objectType == null) {
            log.error("input is null");
            return null;
        }

        InfoObjectTypeVO infoObjectTypeVO = ObjectTypeTsr.updateInputBeanToVo(objectType);
        infoObjectTypeVO = infoObjectTypeDefService.updateObjectTypeDef(tenantId, objectTypeId, infoObjectTypeVO);
        if (infoObjectTypeVO == null) {
            log.error("update object type failed");
            return null;
        }

        return ObjectTypeTsr.voToUpdateOutputBean(infoObjectTypeVO);
    }

    /**
     * 禁用对象模型
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public boolean deleteObjectType(String tenantId, String objectTypeId) throws DataServiceModelRuntimeException,
            CimDataEngineRuntimeException {
        return infoObjectTypeDefService.deleteObjectTypeDef(tenantId, objectTypeId);
    }

    /**
     * 查询object type id 是否可用，根据是否已创建以该id命名的表判断
     *
     * @param objectTypeId
     * @return
     */
    public boolean isObjectTypeIdAvailable(String objectTypeId) {
        CimDataSpace ids = null;
        try {
            ids = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (ids.hasInheritFactType(objectTypeId)) {
                return false;
            } else {
                return true;
            }
        } finally {
            if (ids != null) {
                ids.closeSpace();
            }
        }
    }


    /**
     * 获取指定对象的属性集定义
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetName  属性集名称，若为空则获取所有属性集定义，包含Bese属性集
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public List<DataSetEntity> getDataSetDef(String tenantId, String objectTypeId, String dataSetName)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        List<DataSetEntity> dataSetEntities = new ArrayList<>();

        List<DatasetVO> datasetVOS = infoObjectTypeDefService.getDataSetDef(tenantId, objectTypeId, dataSetName);
        if (datasetVOS == null) {
            return dataSetEntities;
        }
        for (DatasetVO datasetVO : datasetVOS) {
            dataSetEntities.add(DataSetTsr.voToEntityV2(datasetVO, true));
        }
        return dataSetEntities;
    }

    public ObjectTypeQueryOutput queryObjectTypes(String tenantId, ObjectTypeQueryInput queryInputBean)
            throws DataServiceModelRuntimeException {
        return infoObjectTypeDefService.queryObjectTypes(tenantId, queryInputBean, false, false);
    }

    public List<RelationshipEntity> queryRelationships(String tenantId, String objectTypeId,
                                                       ObjectTypeRelationshipQueryInput queryInput)
            throws CimDataEngineRuntimeException {
        return relationshipsService.getRelationshipsByObjectTypeName(tenantId, objectTypeId, queryInput);
    }

    public List<CheckAndAddObjectTypeOutputBean> checkAndCreateObjectDef(String tenantId,
                                                                         List<CheckAndAddObjectTypeInputBean> typeInputBeans) {
        if (StringUtils.isBlank(tenantId)) {
            log.info("tenantId is blank, default tenant is used: [{}]",
                    BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME);
            tenantId = BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME;
        }
        List<CheckAndAddObjectTypeOutputBean> typeOutputBeans = new ArrayList<>();
        CimDataSpace cds = null;
        String message = "success";
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            for (CheckAndAddObjectTypeInputBean objectTypeInputBean : typeInputBeans) {
                String objectId = objectTypeInputBean.getName();
                CheckAndAddObjectTypeOutputBean typeOutputBean = new CheckAndAddObjectTypeOutputBean(objectId);
                typeOutputBeans.add(typeOutputBean);
                try {
                    if (StringUtils.isNotBlank(objectId)) {
                        InfoObjectTypeVO objectTypeVO = ObjectTypeTsr.checkAndAddInputBeanToVo(objectTypeInputBean);
                        if (cds.hasInheritFactType(objectId)) {
                            log.debug("object type is exists: [{}]", objectId);
                            Fact targetCimObjectTypeFact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds,
                                    objectId);

                            if (!CommonOperationUtil.isTenantContainsData(tenantId, targetCimObjectTypeFact)) {
                                CommonOperationUtil.addToBelongingTenant(cds, tenantId, targetCimObjectTypeFact);
                                log.debug("share object type to tenant [{}]", tenantId);
                            }

                            if (objectTypeInputBean.getClean()) {
                                log.warn("!!!clean up object type: [{}]", objectId);
//                                cds.getInheritFactType(objectId).removeContainedFacts();
                                CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(
                                        CimConstants.defauleSpaceName, tenantId);
                                modelCore.setCimDataSpace(cds);
                                InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectId);

                                ExploreParameters ep = new ExploreParameters();
                                ep.setStartPage(1);
                                ep.setEndPage(2);
                                ep.setPageSize(Integer.MAX_VALUE);
                                InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(ep);

                                if (retrieveResult != null && !CollectionUtils.isEmpty(
                                        retrieveResult.getInfoObjects())) {
                                    log.info("===delete object size: [{}]", retrieveResult.getInfoObjects().size());
                                    for (InfoObject object : retrieveResult.getInfoObjects()) {
                                        cds.removeFact(object.getObjectInstanceRID());
                                    }
                                } else {
                                    log.info("===delete object size: [0]");
                                }
                            } else {
                                log.info("reserve old data: [{}]", objectId);
                            }

                            CheckAndAddDataSetInputBean dataSetInputBean = objectTypeInputBean.getDataSets().get(0);
                            if (dataSetInputBean.getCreate()) {
                                log.debug("add new dataset: [{}]", dataSetInputBean);
                                DatasetDef datasetDef = dataSetDefService.addDataSetAndPropertyDef(tenantId, objectId
                                        , cds, objectTypeVO.getLinkedDatasets().get(0));
                                if (datasetDef != null && StringUtils.isNotBlank(datasetDef.getDatasetRID())) {
                                    typeOutputBean.setSuccess(true);
                                } else {
                                    log.error("object type name must not blank: [{}]", objectTypeInputBean);
                                    message = "add dataset failed";
                                    typeOutputBean.setSuccess(false);
                                }
                            } else {
                                log.debug("no dataset should be added");
                                typeOutputBean.setSuccess(true);
                            }
                        } else {
                            log.debug("add new object: [{}]", objectId);
                            String tmpObjId = infoObjectTypeDefService.addObjectTypeDef(cds, tenantId,
                                    objectTypeVO);
                            if (StringUtils.isNotBlank(tmpObjId)) {
                                CheckAndAddDataSetInputBean dataSetInputBean = objectTypeInputBean.getDataSets().get(0);
                                log.debug("add new dataset: [{}]", dataSetInputBean);
                                DatasetDef datasetDef = dataSetDefService.addDataSetAndPropertyDef(tenantId, objectId
                                        , cds, objectTypeVO.getLinkedDatasets().get(0));
                                if (datasetDef != null && StringUtils.isNotBlank(datasetDef.getDatasetRID())) {
                                    typeOutputBean.setSuccess(true);
                                } else {
                                    log.error("object type name must not blank: [{}]", objectTypeInputBean);
                                    message = "add dataset failed";
                                    typeOutputBean.setSuccess(false);
                                }
                                if (StringUtils.isNotBlank(objectTypeInputBean.getCommonTag())) {
                                    CIMModelCore modelCore =
                                            ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                                                    tenantId);
                                    CommonTags commonTags = modelCore.getCommonTags();
                                    CommonTag ctag = commonTags.getTag(objectTypeInputBean.getCommonTag());
                                    if (ctag == null) {
                                        CommonTagVO tagVO = new CommonTagVO();
                                        tagVO.setTagName(objectTypeInputBean.getCommonTag());
                                        tagVO.setTagDesc(objectTypeInputBean.getCommonTag());
                                        ctag = commonTags.addTag(tagVO);
                                    }
                                    modelCore.setCimDataSpace(cds);
                                    InfoObjectDef objectDef = modelCore.getInfoObjectDef(tmpObjId);
                                    objectDef.attachCommonTag(defaultTagAttachInfo(), ctag.getTagRID());
                                } else {
                                    log.info("attach tag is blank");
                                }
                            } else {
                                log.error("add object type failed: [{}]", objectTypeInputBean);
                                message = "add object type failed";
                                typeOutputBean.setSuccess(false);
                            }
                        }
                    } else {
                        log.error("object type name must not blank: [{}]", objectTypeInputBean);
                        message = "object type name is mandatory";
                        typeOutputBean.setSuccess(false);
                    }
                } catch (Exception e) {
                    log.error("add object and dataset failed", e);
                    message = "add object and dataset failed";
                    typeOutputBean.setSuccess(false);
                }
                typeOutputBean.setMessage(message);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return typeOutputBeans;
    }

    public UniversalDimensionAttachInfo defaultTagAttachInfo() {
        return new UniversalDimensionAttachInfo(CimConstants.OBJECT_TYPE_AND_TAG_RELATION_TYPE_DEFAULT,
                RelationDirection.FROM, new HashMap<>());
    }

    public void setInfoObjectTypeDefService(InfoObjectTypeDefService infoObjectTypeDefService) {
        this.infoObjectTypeDefService = infoObjectTypeDefService;
    }

    public void setDataSetDefService(DataSetDefService dataSetDefService) {
        this.dataSetDefService = dataSetDefService;
    }
}
