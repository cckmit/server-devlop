package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntityWithoutDataSet;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.InheritFactType;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationType;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.RelationshipMappingFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationshipDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationshipDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.ApiRunTimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.transverter.ObjectTypeTsr;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryOutput;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.glodon.pcop.cimsvc.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jodconverter.office.utils.Lo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InfoObjectTypeDefService {
    private static final Logger log = LoggerFactory.getLogger(InfoObjectTypeDefService.class);

    public static final String UNDEFINED_INDUSTRY = "未定义分类";
    public static final String OBJECT_TYPE_NOT_FOUND = "Object type definition of objectTypeId={} not found";
    public static final String DATA_SET_NOT_FOUND = "No data set is link with object type of objectTypeId={}";

    /**
     * 新增行业分类模型定义，默认关联"未定义分类"
     *
     * @param tenantId
     * @param objectTypeVO
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public String addObjectTypeDef(String tenantId, InfoObjectTypeVO objectTypeVO) throws
                                                                                   DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        InfoObjectDef objectDef;
        if (StringUtils.isBlank(objectTypeVO.getParentObjectTypeName())) {
            objectDef = objectDefs.addRootInfoObjectDef(objectTypeVO);
        } else {
            objectDef = objectDefs.addChildInfoObjectDef(objectTypeVO, objectTypeVO.getParentObjectTypeName());
        }
        if (StringUtils.isNotBlank(objectTypeVO.getIndustryTypeId())) {
            objectDef.linkIndustryType(objectTypeVO.getIndustryTypeId());
        } else {
            IndustryTypeVO defaultIndustry = IndustryTypeFeatures.getIndustryTypeByName(CimConstants.defauleSpaceName,
                    UNDEFINED_INDUSTRY);
            if (defaultIndustry != null) {
                objectDef.linkIndustryType(defaultIndustry.getIndustryTypeId());
            } else {
                log.error("默认行业分类：{}，不存在", UNDEFINED_INDUSTRY);
            }
        }
        return objectDef.getObjectTypeName();
    }

    public String addObjectTypeDef(CimDataSpace cds, String tenantId, InfoObjectTypeVO objectTypeVO) throws
                                                                                                     DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        cimModelCore.setCimDataSpace(cds);
        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        InfoObjectDef objectDef;
        if (StringUtils.isBlank(objectTypeVO.getParentObjectTypeName())) {
            objectDef = objectDefs.addRootInfoObjectDef(objectTypeVO);
        } else {
            objectDef = objectDefs.addChildInfoObjectDef(objectTypeVO, objectTypeVO.getParentObjectTypeName());
        }
        if (StringUtils.isNotBlank(objectTypeVO.getIndustryTypeId())) {
            objectDef.linkIndustryType(objectTypeVO.getIndustryTypeId());
        } else {
            String defaultIndustryRid = IndustryTypeFeatures.getDefaultIndustryTypeRid(cds, UNDEFINED_INDUSTRY);
            if (StringUtils.isNotBlank(defaultIndustryRid)) {
                objectDef.linkIndustryType(defaultIndustryRid);
            } else {
                log.error("默认行业分类：{}，不存在", UNDEFINED_INDUSTRY);
            }
        }
        return objectDef.getObjectTypeName();
    }

    /**
     * 获取指定的对象类型信息
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     */
    public InfoObjectTypeVO getObjectTypeDef(String tenantId, String objectTypeId, boolean isIncludedDataSet,
            boolean isIncludedProperty) throws DataServiceModelRuntimeException {//NOSONAR
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);

        InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName,
                objectTypeId, false, false);
        if (objectTypeVO == null) {
            log.error(OBJECT_TYPE_NOT_FOUND, objectTypeId);
            return null;
        }

        List<IndustryType> industryTypeList = infoObjectDef.getLinkedIndustryTypes();
        if (industryTypeList != null && industryTypeList.size() > 0) {
            objectTypeVO.setIndustryTypeId(industryTypeList.get(0).getIndustryTypeRID());
        }

        InheritFactType inheritFactType = InfoObjectFeatures.getAncestorObjectTypes(CimConstants.defauleSpaceName,
                objectTypeId);
        if (inheritFactType != null) {
            objectTypeVO.setParentObjectTypeName(inheritFactType.getTypeName());
        }

        List<DatasetVO> datasetVOList = new ArrayList<>();
        if (isIncludedDataSet) {
            try {
                List<DatasetDef> datasetDefList = infoObjectDef.getDatasetDefs();
                if (datasetDefList != null && datasetDefList.size() > 0) {
                    for (DatasetDef datasetDef : datasetDefList) {
                        // DatasetVO datasetVO = datasetDef.getDataSetVO(isIncludedProperty);
                        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName,
                                datasetDef.getDatasetRID(), isIncludedProperty);
                        if (datasetVO != null) {
                            datasetVOList.add(DatasetFeatures.setInherientInfo(CimConstants.defauleSpaceName, datasetVO,
                                    objectTypeId));
                        }
                    }
                } else {
                    log.info(DATA_SET_NOT_FOUND, objectTypeId);
                }
                // datasetVOList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(isIncludedProperty));
                datasetVOList.add(DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName, isIncludedProperty));
                objectTypeVO.setLinkedDatasets(datasetVOList);
            } catch (CimDataEngineRuntimeException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010501, e);
            } catch (CimDataEngineInfoExploreException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010502, e);
            }
        }

        return objectTypeVO;
    }

    public InfoObjectTypeVO getObjectTypeDef(CimDataSpace cds, InfoObjectDef infoObjectDef) throws
                                                                                            CimDataEngineRuntimeException,
                                                                                            CimDataEngineInfoExploreException {
        // InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        String objectTypeId = infoObjectDef.getObjectTypeName();
        InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(cds, objectTypeId, false, false);
        if (objectTypeVO == null) {
            log.error(OBJECT_TYPE_NOT_FOUND, objectTypeId);
            return null;
        }

//        List<IndustryType> industryTypeList = infoObjectDef.getLinkedIndustryTypes();
//        if (industryTypeList != null && industryTypeList.size() > 0) {
//            objectTypeVO.setIndustryTypeId(industryTypeList.get(0).getIndustryTypeRID());
//        }

        InheritFactType inheritFactType = InfoObjectFeatures.getAncestorObjectTypes(cds, objectTypeId);
        if (inheritFactType != null) {
            objectTypeVO.setParentObjectTypeName(inheritFactType.getTypeName());
        }

        return objectTypeVO;
    }

    /**
     * 获取指定的对象类型信息，根据配置获取属性集和属性信息
     *
     * @param tenantId
     * @param objectTypeId
     * @param isIncludedDataSet
     * @param isIncludedProperty
     * @param dataSetType
     * @return
     */

//    @Cacheable(cacheNames = "CIMModelCore.InfoObjectDef")
    public InfoObjectTypeVO getObjectTypeDef(String tenantId, String objectTypeId, boolean isIncludedDataSet,
            boolean isIncludedProperty, String dataSetType) {//NOSONAR
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        InfoObjectTypeVO objectTypeVO = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);

            objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(cds, objectTypeId, false, false);
            if (objectTypeVO == null || objectTypeVO.isDisabled()) {
                log.error("Object type definition of objectTypeId={} not found or is disabled", objectTypeId);
                return null;
            }

            if (infoObjectDef == null) {
                log.error("infoObjectDef of {} not found", objectTypeId);
                return null;
            }

            List<IndustryType> industryTypeList = infoObjectDef.getLinkedIndustryTypes();
            if (industryTypeList != null && industryTypeList.size() > 0) {
                objectTypeVO.setIndustryTypeId(industryTypeList.get(0).getIndustryTypeRID());
            }

            InheritFactType inheritFactType = InfoObjectFeatures.getAncestorObjectTypes(cds, objectTypeId);
            if (inheritFactType != null) {
                objectTypeVO.setParentObjectTypeName(inheritFactType.getTypeName());
            }

            List<DatasetVO> datasetVOList = new ArrayList<>();
            if (isIncludedDataSet) {
                try {
                    List<DatasetDef> datasetDefList = null;
                    if (StringUtils.isBlank(dataSetType) || dataSetType.toUpperCase().equals("INSTANCE")) {
                        datasetDefList = infoObjectDef.getDatasetDefs();
                        datasetVOList.add(DatasetFeatures.getBaseDatasetVO(cds, isIncludedProperty));
                    } else if (dataSetType.toUpperCase().equals("OBJECT")) {
                        datasetDefList = infoObjectDef.getDefinitionDatasetDefs();
                    } else {
                        log.error("unrecognized data set type: {}", dataSetType);
                    }
                    if (datasetDefList != null && datasetDefList.size() > 0) {
                        for (DatasetDef datasetDef : datasetDefList) {
                            // DatasetVO datasetVO = datasetDef.getDataSetVO(isIncludedProperty);
                            DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(cds, datasetDef.getDatasetRID(),
                                    isIncludedProperty);
                            if (datasetVO != null) {
                                datasetVOList.add(DatasetFeatures.setInherientInfo(cds, datasetVO, objectTypeId));
                            }
                        }
                    } else {
                        log.info(DATA_SET_NOT_FOUND, objectTypeId);
                    }
                    // datasetVOList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(isIncludedProperty));
                    // datasetVOList.add(DatasetFeatures.getBaseDatasetVO(cds, isIncludedProperty));
                    objectTypeVO.setLinkedDatasets(datasetVOList);
                } catch (CimDataEngineRuntimeException e) {
                    throw new ApiRunTimeException(CodeAndMsg.E05010501, e);
                } catch (CimDataEngineInfoExploreException e) {
                    throw new ApiRunTimeException(CodeAndMsg.E05010502, e);
                }
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
        return objectTypeVO;
    }

    public InfoObjectTypeVO getObjectTypeDef(CimDataSpace ids, String tenantId, String objectTypeId,
            boolean isIncludedDataSet, boolean isIncludedProperty) throws DataServiceModelRuntimeException,
                                                                          CimDataEngineRuntimeException,
                                                                          CimDataEngineInfoExploreException {//NOSONAR
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        cimModelCore.setCimDataSpace(ids);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(ids, objectTypeId, false, false);
        if (objectTypeVO == null) {
            log.error(OBJECT_TYPE_NOT_FOUND, objectTypeId);
            return null;
        }

        List<IndustryType> industryTypeList = infoObjectDef.getLinkedIndustryTypes();
        if (industryTypeList != null && industryTypeList.size() > 0) {
            objectTypeVO.setIndustryTypeId(industryTypeList.get(0).getIndustryTypeRID());
        }

        InheritFactType inheritFactType = InfoObjectFeatures.getAncestorObjectTypes(ids, objectTypeId);
        if (inheritFactType != null) {
            objectTypeVO.setParentObjectTypeName(inheritFactType.getTypeName());
        }

        List<DatasetVO> datasetVOList = new ArrayList<>();
        if (isIncludedDataSet) {
            try {
                List<DatasetDef> datasetDefList = infoObjectDef.getDatasetDefs();
                if (datasetDefList != null && datasetDefList.size() > 0) {
                    for (DatasetDef datasetDef : datasetDefList) {
                        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(ids, datasetDef.getDatasetRID(),
                                isIncludedProperty);
                        if (datasetVO != null) {
                            datasetVOList.add(DatasetFeatures.setInherientInfo(ids, datasetVO, objectTypeId));
                        }
                    }
                } else {
                    log.info(DATA_SET_NOT_FOUND, objectTypeId);
                }
                datasetVOList.add(DatasetFeatures.getBaseDatasetVO(ids, isIncludedProperty));
                objectTypeVO.setLinkedDatasets(datasetVOList);
            } catch (CimDataEngineRuntimeException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010501, e);
            } catch (CimDataEngineInfoExploreException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010502, e);
            }
        }

        return objectTypeVO;
    }

    /**
     * 更新对象模型名称及其关联的行业分类
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectTypeVO
     * @return
     */
//    @CacheEvict(cacheNames = "CIMModelCore.InfoObjectDef")
    public InfoObjectTypeVO updateObjectTypeDef(String tenantId, String objectTypeId,
            InfoObjectTypeVO objectTypeVO) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        if (objectDefs.updateInfoObjectDef(objectTypeId, objectTypeVO)) {
            return getObjectTypeDef(tenantId, objectTypeId, false, false);
        } else {
            log.error("Object type update failed, objectTypeId={}", objectTypeId);
            return null;
        }
    }


    /**
     * 禁用对象模型
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     * @throws DataServiceModelRuntimeException
     */
//    @CacheEvict(cacheNames = "CIMModelCore.InfoObjectDef")
    public boolean deleteObjectTypeDef(String tenantId, String objectTypeId) throws DataServiceModelRuntimeException,
                                                                                    CimDataEngineRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        return objectDefs.removeInfoObjectDef(objectTypeId);
//        return objectDefs.disableInfoObjectDef(objectTypeId);
    }

    /**
     * 获取与指定对象类型相关的所有对象关系
     *
     * @param objectTypeId
     * @param relationRole
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<RelationshipMappingVO> getRelationshipsByObjectTypeName(String tenantId, String objectTypeId,
            String relationRole) throws CimDataEngineRuntimeException {
        List<RelationshipMappingVO> mappingVOList = new ArrayList<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        RelationshipDefs relationshipDefs = cimModelCore.getRelationshipDefs();
        if (relationshipDefs != null) {
            List<RelationshipDef> relationshipDefList;
            switch (relationRole) {
                case "SOURCE":
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(objectTypeId,
                            RelationshipMappingFeatures.RelationshipRole.SOURCE);
                    break;
                case "TARGET":
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(objectTypeId,
                            RelationshipMappingFeatures.RelationshipRole.TARGET);
                    break;
                default:
                    relationshipDefList = relationshipDefs.getRelationshipDefsByInvolvedObjectType(objectTypeId,
                            RelationshipMappingFeatures.RelationshipRole.BOTH);
                    break;
            }
            if (relationshipDefList != null) {
                for (RelationshipDef relationshipDef : relationshipDefList) {
                    RelationshipMappingVO mappingVO = RelationshipMappingFeatures.getRelationshipMappingById(
                            CimConstants.defauleSpaceName, relationshipDef.getRelationshipRID());
                    if (mappingVO != null) {
                        mappingVOList.add(mappingVO);
                    }
                }
            }
        }
        return mappingVOList;
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
    public List<DatasetVO> getDataSetDef(String tenantId, String objectTypeId, String dataSetName) throws
                                                                                                   DataServiceModelRuntimeException,
                                                                                                   EntityNotFoundException {
        List<DatasetVO> voList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);

        if (StringUtils.isNotBlank(dataSetName)) {
            if (dataSetName.equals(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME)) {
                // voList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(true));
                // voList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(true));
                voList.add(DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName));
            } else {
                DatasetDef datasetDef = infoObjectDef.getDatasetDef(dataSetName);
                if (datasetDef != null) {
                    // voList.add(datasetDef.getDataSetVO(true));
                    voList.add(
                            DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, datasetDef.getDatasetRID(),
                                    true));
                }
            }
        } else {
            List<DatasetDef> dataSetDefList = infoObjectDef.getDatasetDefs();
            if (dataSetDefList != null) {
                for (DatasetDef def : dataSetDefList) {
                    // voList.add(def.getDataSetVO(true));
                    voList.add(
                            DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, def.getDatasetRID(), true));
                }
            }
            // voList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(true));
            voList.add(DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName));
        }

        return voList;
    }


    public ObjectTypeQueryOutput queryObjectTypes(String tenantId, ObjectTypeQueryInput queryInputBean,
            boolean isIncludedDataSet, boolean isIncludedProperty) {//NOSONAR
        ObjectTypeQueryOutput queryOutput = new ObjectTypeQueryOutput();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            cimModelCore.setCimDataSpace(cds);

            InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
            ExploreParameters exploreParameters = new ExploreParameters();
            FilteringItem descFilteringItem = null;
            FilteringItem idFilteringItem = null;
            if (StringUtils.isNotBlank(queryInputBean.getObjectTypeDesc())) {
                descFilteringItem = new SimilarFilteringItem("infoObjectTypeDesc", queryInputBean.getObjectTypeDesc(),
                        SimilarFilteringItem.MatchingType.Contain, false);
            }
            if (StringUtils.isNotBlank(queryInputBean.getObjectTypeId())) {
                idFilteringItem = new SimilarFilteringItem("infoObjectTypeName", queryInputBean.getObjectTypeId(),
                        SimilarFilteringItem.MatchingType.Contain, false);
            }
            if (descFilteringItem != null) {
                exploreParameters.setDefaultFilteringItem(descFilteringItem);
                if (idFilteringItem != null) {
                    exploreParameters.addFilteringItem(idFilteringItem, ExploreParameters.FilteringLogic.AND);
                }
            } else if (idFilteringItem != null) {
                exploreParameters.setDefaultFilteringItem(idFilteringItem);
            } else {
                log.info("no action");
            }

            List<String> sortProperties = new ArrayList<>();
            if (queryInputBean.getSortAttributes() != null && queryInputBean.getSortAttributes().size() > 0) {
                for (String st : queryInputBean.getSortAttributes()) {
                    if (st.toUpperCase().equals("ID") || st.toUpperCase().equals("NAME")) {
                        sortProperties.add("infoObjectTypeName");
                    } else if (st.toUpperCase().equals("DESC")) {
                        sortProperties.add("infoObjectTypeDesc");
                    } else {
                        sortProperties.add(st);
                    }
                }
                exploreParameters.setSortingLogic(queryInputBean.getSortingLogic());
                exploreParameters.setSortAttributes(sortProperties);
            }

            int startPage = queryInputBean.getStartPage();
            int entPage = queryInputBean.getEndPage();
            int pageSize = queryInputBean.getPageSize();

//            int startIdx = 0;
//            int endIdx = 10;

            if (startPage > 0 && entPage > startPage && pageSize > 0) {
//                startIdx = (startPage - 1) * pageSize;
//                endIdx = (entPage - 1) * pageSize;
                exploreParameters.setStartPage(startPage);
                exploreParameters.setEndPage(entPage);
                exploreParameters.setPageSize(pageSize);
            } else {
                exploreParameters.setStartPage(1);
                exploreParameters.setEndPage(2);
                exploreParameters.setPageSize(50);
            }
            log.info("start query : {}", DateUtil.getCurrentDateReadable());
            List<InfoObjectDef> infoObjectDefList = infoObjectDefs.queryAllInfoObjectDefs(exploreParameters);

            log.info("finish query : {}", DateUtil.getCurrentDateReadable());

            List<InfoObjectTypeVO> infoObjectTypeVOS = new ArrayList<>();
            for (InfoObjectDef tmpInfoObjectDef : infoObjectDefList) {
                log.debug("info object type: {}", tmpInfoObjectDef.getObjectTypeName());
                try {
                    infoObjectTypeVOS.add(getObjectTypeDef(cds, tmpInfoObjectDef));
                } catch (Exception e) {
                    log.error("get object type info failed", e);
                }
            }
            log.info("finish get object info : {}", DateUtil.getCurrentDateReadable());
            List<ObjectTypeEntityWithoutDataSet> objectTypeEntities = new ArrayList<>();
            for (InfoObjectTypeVO objectTypeVO : infoObjectTypeVOS) {
                ObjectTypeEntityWithoutDataSet objectTypeEntity = ObjectTypeTsr.voToEntity(objectTypeVO);
                if (objectTypeEntity != null && StringUtils.isNotBlank(objectTypeEntity.getId())) {
                    objectTypeEntities.add(objectTypeEntity);
                }
            }
            //total size
            InformationExplorer ie = cds.getInformationExplorer();
            exploreParameters.setStartPage(1);
            exploreParameters.setEndPage(2);
            exploreParameters.setPageSize(10 * 10000);
            exploreParameters.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);
//            List<Fact> infoObjectDefStatusFactList = ie.discoverFacts(exploreParameters);
            List<Relationable> resultRelationableDataList = null;
            List<String> tenantIds = new ArrayList<>();
            tenantIds.add(BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME);
            tenantIds.add(tenantId);
            resultRelationableDataList = ie.exploreDirectlyRelatedRelationables(InformationType.FACT,
                    RelationDirection.FROM, tenantIds, BusinessLogicConstant.CIM_BUILDIN_TENANT_ID,
                    exploreParameters);

            if (CollectionUtils.isNotEmpty(resultRelationableDataList)) {
                queryOutput.setTotalCount((long) resultRelationableDataList.size());
            } else {
                queryOutput.setTotalCount(0L);
            }

            queryOutput.setObjectTypes(objectTypeEntities);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return queryOutput;
    }
}
