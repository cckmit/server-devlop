package com.glodon.pcop.cimsvc.service.tag;

import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.tag.CommonTagAddInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagBaseInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagOutputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagTrasnlater;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.DataEngineException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.output.InstancesByTagOutputBean;
import com.glodon.pcop.cimsvc.model.transverter.ObjectTypeTsr;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonTagService {
    private static final Logger log = LoggerFactory.getLogger(CommonTagService.class);

    public CommonTagOutputBean addCommonTag(String tenantId,
                                            String userId,
                                            CommonTagAddInputBean tagAddInput)
            throws DataServiceModelRuntimeException {
        CimDataSpace cds = null;
        CommonTagOutputBean tagOutputBean = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.addTag(CommonTagTrasnlater.updateInputBeanToVo(tagAddInput));
            tagOutputBean = CommonTagTrasnlater.addOutputVoToBean(commonTag);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBean;
    }

    public CommonTagOutputBean updateCommonTag(String tenantId,
                                               String userId,
                                               String tagName,
                                               CommonTagBaseInputBean tagUpdateInput)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        CimDataSpace cds = null;
        CommonTagOutputBean tagOutputBean = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                tagOutputBean = CommonTagTrasnlater.addOutputVoToBean(commonTags.updateTag(commonTag.getTagRID(),
                        CommonTagTrasnlater.updateInputBeanToVo(tagUpdateInput)));
            } else {
                log.error("common tag not found: [{}]",
                        tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBean;
    }

    public boolean deleteCommonTag(String tenantId,
                                   String userId,
                                   String tagName)
            throws EntityNotFoundException {
        CimDataSpace cds = null;
        boolean tagOutputBean = false;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                tagOutputBean = commonTags.removeTag(commonTag.getTagRID());
            } else {
                log.error("common tag not found: [{}]",
                        tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBean;
    }

    public CommonTagOutputBean getCommonTag(String tenantId,
                                            String userId,
                                            String tagName)
            throws EntityNotFoundException {
        CimDataSpace cds = null;
        CommonTagOutputBean tagOutputBean = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                tagOutputBean = CommonTagTrasnlater.addOutputVoToBean(commonTag);
            } else {
                log.error("common tag not found: [{}]",
                        tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBean;
    }

    public CommonTagOutputBean getParentCommonTag(String tenantId,
                                                  String userId,
                                                  String tagName)
            throws EntityNotFoundException {
        CimDataSpace cds = null;
        CommonTagOutputBean tagOutputBean = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                CommonTag parentTag = commonTag.getParentTag();
                if (parentTag != null) {
                    tagOutputBean = CommonTagTrasnlater.addOutputVoToBean(parentTag);
                } else {
                    log.info("no parent common tag: [{}]",
                            tagName);
                }
            } else {
                log.error("common tag not found: [{}]",
                        tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBean;
    }

    public List<CommonTagOutputBean> getChildCommonTags(String tenantId,
                                                        String userId,
                                                        String tagName)
            throws EntityNotFoundException {
        CimDataSpace cds = null;
        List<CommonTagOutputBean> tagOutputBeanList = new ArrayList<>();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                List<CommonTag> childTags = commonTag.getChildTags();
                if (CollectionUtils.isNotEmpty(childTags)) {
                    for (CommonTag ct : childTags) {
                        tagOutputBeanList.add(CommonTagTrasnlater.addOutputVoToBean(ct));
                    }
                } else {
                    log.info("no child common tag: [{}]",
                            tagName);
                }
            } else {
                log.error("common tag not found: [{}]",
                        tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBeanList;
    }


    public List<CommonTagOutputBean> getCommonTagsByDataSetRid(String tenantId,
                                                               String userId,
                                                               String dataSetRid,
                                                               String relationType,
                                                               RelationDirection relationDirection)
            throws DataEngineException, CimDataEngineRuntimeException {
        CimDataSpace cds = null;
        List<CommonTagOutputBean> tagOutputBeanList = new ArrayList<>();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            Fact dataSetFact;
            try {
                dataSetFact = cds.getFactById(dataSetRid);
            } catch (CimDataEngineRuntimeException e) {
                log.error("get dataset by rid failed");
                throw new DataEngineException(EnumWrapper.CodeAndMsg.E05010506,
                        e.getMessage());
            }

            List<Relation> dataSetRelations = dataSetFact.getAllSpecifiedRelations(relationType,
                    relationDirection);

            if (CollectionUtils.isNotEmpty(dataSetRelations)) {
                for (Relation relation : dataSetRelations) {
                    Relationable fromRelationable = relation.getFromRelationable();
                    Relationable toRelationable = relation.getToRelationable();

                    Relationable tagRelationable;
                    if (fromRelationable.getId().equalsIgnoreCase(dataSetRid)) {
                        tagRelationable = toRelationable;
                    } else {
                        tagRelationable = fromRelationable;
                    }
                    if (tagRelationable instanceof Dimension) {
                        String dimentionType = ((Dimension) tagRelationable).getType();
                        if (dimentionType.equalsIgnoreCase(BusinessLogicConstant.CIM_TAG_DIMENSION_TYPE_NAME)) {
                            CommonTag tmpTag = commonTags.getTag(tagRelationable.getProperty(
                                    BusinessLogicConstant.CIM_TAG_DIMENSION_FIELDNAME_TAG_NAME).getPropertyValue().toString());
                            if (tmpTag != null) {
                                tagOutputBeanList.add(CommonTagTrasnlater.addOutputVoToBean(tmpTag));
                            }
                        }
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBeanList;
    }


    public List<CommonTagOutputBean> getCommonTagsByObjectType(String tenantId,
                                                               String userId,
                                                               String objectTypeId,
                                                               String relationType,
                                                               RelationDirection relationDirection)
            throws DataEngineException, CimDataEngineRuntimeException, EntityNotFoundException {
        CimDataSpace cds = null;
        List<CommonTagOutputBean> tagOutputBeanList = new ArrayList<>();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            Fact objectTypeStatuFact;
            try {
                FilteringItem equalFilter = new EqualFilteringItem("infoObjectTypeName",
                        objectTypeId);
                ExploreParameters ep = new ExploreParameters();
                ep.setDefaultFilteringItem(equalFilter);
                ep.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);

                List<Fact> factList = cds.getInformationExplorer().discoverFacts(ep);
                if (CollectionUtils.isNotEmpty(factList)) {
                    objectTypeStatuFact = factList.get(0);
                } else {
                    log.error("object type status of [{}], not found or not belong to this tennat",
                            objectTypeId);
                    throw new EntityNotFoundException("object type not found or not belong to this tenant");
                }
            } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
                log.error("get dataset by rid failed");
                throw new DataEngineException(EnumWrapper.CodeAndMsg.E05010506,
                        e.getMessage());
            }

            List<Relation> dataSetRelations = objectTypeStatuFact.getAllSpecifiedRelations(relationType,
                    relationDirection);

            if (CollectionUtils.isNotEmpty(dataSetRelations)) {
                for (Relation relation : dataSetRelations) {
                    Relationable fromRelationable = relation.getFromRelationable();
                    Relationable toRelationable = relation.getToRelationable();

                    Relationable tagRelationable;
                    if (fromRelationable.getId().equalsIgnoreCase(objectTypeStatuFact.getId())) {
                        tagRelationable = toRelationable;
                    } else {
                        tagRelationable = fromRelationable;
                    }
                    if (tagRelationable instanceof Dimension) {
                        String dimentionType = ((Dimension) tagRelationable).getType();
                        if (dimentionType.equalsIgnoreCase(BusinessLogicConstant.CIM_TAG_DIMENSION_TYPE_NAME)) {
                            tagOutputBeanList.add(CommonTagTrasnlater.addOutputVoToBean(
                                    commonTags.getTag(tagRelationable.getProperty(
                                            BusinessLogicConstant.CIM_TAG_DIMENSION_FIELDNAME_TAG_NAME).getPropertyValue().toString())));
                        }
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return tagOutputBeanList;
    }

    /**
     * 查询与标签关联的对象模型定义，不包含属性集和属性定义
     *
     * @param tenantId
     * @param userId
     * @param tagName
     * @param relationType
     * @param relationDirection
     * @return
     * @throws DataEngineException
     * @throws CimDataEngineRuntimeException
     * @throws EntityNotFoundException
     */
    public List<ObjectTypeEntity> getObjectTypesByCommonTag(String tenantId,
                                                            String userId,
                                                            String tagName,
                                                            String relationType,
                                                            RelationDirection relationDirection)
            throws EntityNotFoundException {
        List<ObjectTypeEntity> objectTypeEntityList = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                List<InfoObjectDef> infoObjectDefList = commonTag.getAttachedInfoObjectDefs(relationType,
                        relationDirection);
                if (CollectionUtils.isNotEmpty(infoObjectDefList)) {
                    for (InfoObjectDef objectDef : infoObjectDefList) {
                        try {
                            InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(cds,
                                    objectDef.getObjectTypeName(), false, false);
                            objectTypeEntityList.add(ObjectTypeTsr.voToEntity(objectTypeVO, false, false));
                        } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
                            log.error("get object type vo failed: [{}]", objectDef.getObjectTypeName());
                        }
                    }
                } else {
                    log.info(
                            "no object type related with this common tag: tagName=[{}], relationType=[{}], relationDirection=[{}]",
                            tagName, relationType, relationDirection);
                }
            } else {
                log.error("common tag not found: [{}]", tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return objectTypeEntityList;
    }


    /**
     * 查询与标签关联的实例，只包含基本属性
     *
     * @param tenantId
     * @param userId
     * @param tagName
     * @param relationType
     * @param relationDirection
     * @return
     * @throws EntityNotFoundException
     */
    public List<InstancesByTagOutputBean> getInstanceByCommonTag(String tenantId,
                                                                 String userId,
                                                                 String tagName,
                                                                 String relationType,
                                                                 RelationDirection relationDirection)
            throws EntityNotFoundException {
        List<InstancesByTagOutputBean> instancesOutputBeans = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            CommonTag commonTag = commonTags.getTag(tagName);
            if (commonTag != null) {
                List<InfoObject> infoObjectList = commonTag.getAttachedInfoObjects(relationType, relationDirection);
                if (CollectionUtils.isNotEmpty(infoObjectList)) {
                    for (InfoObject infoObject : infoObjectList) {
                        try {
                            InstancesByTagOutputBean tmpInstanceBean = new InstancesByTagOutputBean(
                                    infoObject.getObjectTypeName(), infoObject.getObjectInstanceRID(),
                                    infoObject.getInfo());
                            instancesOutputBeans.add(tmpInstanceBean);
                        } catch (DataServiceModelRuntimeException e) {
                            log.error("get instance base info failed: [{}]", infoObject.getObjectInstanceRID());
                        }
                    }
                } else {
                    log.info(
                            "no object type related with this common tag: tagName=[{}], relationType=[{}], relationDirection=[{}]",
                            tagName, relationType, relationDirection);
                }
            } else {
                log.error("common tag not found: [{}]", tagName);
                throw new EntityNotFoundException("tag not found or not belong to this tenant");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return instancesOutputBeans;
    }

}
