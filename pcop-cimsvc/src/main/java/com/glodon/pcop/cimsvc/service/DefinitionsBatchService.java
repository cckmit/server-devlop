package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Property;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeRestrictFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.definition.DataSetBatchBean;
import com.glodon.pcop.cimsvc.model.definition.DefinitionsBean;
import com.glodon.pcop.cimsvc.model.definition.IndustryBatchBean;
import com.glodon.pcop.cimsvc.model.definition.ObjectBatchBean;
import com.glodon.pcop.cimsvc.model.definition.PropertyBatchBean;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DefinitionsBatchService {
    private static Logger log = LoggerFactory.getLogger(DefinitionsBatchService.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, String> dataTypeMap = new HashMap<>();

    static {
        dataTypeMap.put("布尔", "BOOLEAN");
        dataTypeMap.put("整型", "INT");
        dataTypeMap.put("短整型", "SHORT");
        dataTypeMap.put("长整型", "LONG");
        dataTypeMap.put("单精度浮点型", "FLOAT");
        dataTypeMap.put("双精度浮点型", "DOUBLE");
        dataTypeMap.put("日期型", "DATE");
        dataTypeMap.put("字符型", "STRING");
        dataTypeMap.put("二进制", "BINARY");
        dataTypeMap.put("比特型", "BYTE");
        dataTypeMap.put("MP3", "MP3");
        dataTypeMap.put("SHP", "SHP");
        dataTypeMap.put("TSDB", "TSDB");
        dataTypeMap.put("DWG", "DWG");
        dataTypeMap.put("文件型", "FILE");
    }

    public static boolean addDefinitionsBatch(String tenantId, String creator, DefinitionsBean definitionsBean) throws JsonProcessingException {
        List<IndustryBatchBean> industryBatchBeans = definitionsBean.getIndustries();
        if (industryBatchBeans == null || industryBatchBeans.size() < 1) {
            log.error("industry type must be provided");
            return false;
        }

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

            Map<Integer, String> industryIds = addIndustryTypesBatch(tenantId, creator, modelCore, cds, definitionsBean.getIndustries());

            addObjectTypesBatch(modelCore, cds, definitionsBean.getObjectTypes(), industryIds);

            Map<Integer, String> dataSetIds = addDataSetBatch(modelCore, cds, definitionsBean.getDataSets());

            addPropertiesBatch(modelCore, cds, definitionsBean.getProperties(), dataSetIds);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return true;
    }

    /**
     * 行业分类是否已经创建
     *
     * @param cds
     * @param industryTypeName
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public static String isIndustryTypeExists(CimDataSpace cds, String industryTypeName) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        InformationExplorer ie = cds.getInformationExplorer();
        ExploreParameters ep = new ExploreParameters();
        ep.setType(BusinessLogicConstant.INDUSTRY_TYPE_DIMENSION_TYPE_NAME);
        ep.setDefaultFilteringItem(new EqualFilteringItem("industryTypeName", industryTypeName));
        List<Dimension> linkedMappingList = ie.discoverDimensions(ep);
        if (linkedMappingList != null && linkedMappingList.size() > 0) {
            return linkedMappingList.get(0).getId();
        } else {
            return null;
        }
    }


    /**
     * 根据index对industry type排序
     *
     * @param inputBeans
     * @return
     */
    public static IndustryBatchBean[] industryBatchBeanSort(List<IndustryBatchBean> inputBeans) {
        IndustryBatchBean[] outputBeans = new IndustryBatchBean[inputBeans.size()];
        for (IndustryBatchBean bean : inputBeans) {
            outputBeans[bean.getIndex() - 1] = bean;
        }
        return outputBeans;
    }


    /**
     * 批量新增行业分类定义
     *
     * @param tenantId
     * @param creator
     * @param modelCore
     * @param cds
     * @param industryBatchBeans
     * @return
     */
    public static Map<Integer, String> addIndustryTypesBatch(String tenantId, String creator, CIMModelCore modelCore, CimDataSpace cds, List<IndustryBatchBean> industryBatchBeans) throws JsonProcessingException { //NOSONAR
        Map<Integer, String> idsMap = new HashMap<>();
        IndustryTypes industryTypes = modelCore.getIndustryTypes();
        IndustryBatchBean[] sortIndustries = industryBatchBeanSort(industryBatchBeans);
        for (int i = 0; i < sortIndustries.length; i++) {
            IndustryBatchBean batchBean = sortIndustries[i];
            log.info("industry bean: {}", objectMapper.writeValueAsString(batchBean));
            try {
                String industryRid = isIndustryTypeExists(cds, batchBean.getName());
                if (StringUtils.isBlank(industryRid) && StringUtils.isNotBlank(batchBean.getName())) {
                    IndustryTypeVO typeVO = new IndustryTypeVO();
                    typeVO.setIndustryTypeName(batchBean.getName());
                    typeVO.setIndustryTypeDesc(batchBean.getName());
                    typeVO.setTenantId(tenantId);
                    typeVO.setCreatorId(creator);
                    typeVO.setParentIndustryTypeId(idsMap.get(batchBean.getParentIndex()));
                    IndustryType industryType;
                    if (StringUtils.isBlank(typeVO.getParentIndustryTypeId())) {
                        industryType = industryTypes.addRootIndustryType(typeVO);
                    } else {
                        industryType = industryTypes.addChildIndustryType(typeVO, typeVO.getParentIndustryTypeId());
                    }
                    if (industryType != null) {
                        idsMap.put(batchBean.getIndex(), industryType.getIndustryTypeRID());
                    } else {
                        log.error("industry type of {} add filed", objectMapper.writeValueAsString(batchBean));
                    }
                } else {
                    log.info("industry of {} is already exists", batchBean.getName());
                    idsMap.put(batchBean.getIndex(), industryRid);
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            } catch (CimDataEngineInfoExploreException e) {
                e.printStackTrace();
            } catch (DataServiceModelRuntimeException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return idsMap;
    }

    /**
     * 对象是否存在
     *
     * @param cds
     * @param objectTypeId
     * @return
     */
    public static String isObjectTypeExists(CimDataSpace cds, String objectTypeId) {
        String objectTypeStatusRecordRid = null;
        try {
            Fact fact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objectTypeId);
            if (fact != null) {
                objectTypeStatusRecordRid = fact.getId();
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }
        return objectTypeStatusRecordRid;
    }

    /**
     * 批量新增对象类型
     *
     * @param modelCore
     * @param cds
     * @param objectBatchBeans
     * @param industryRids
     * @return
     */
    public static List<String> addObjectTypesBatch(CIMModelCore modelCore, CimDataSpace cds, List<ObjectBatchBean> objectBatchBeans, Map<Integer, String> industryRids) { //NOSONAR
        log.info("industry types index and rid map: {}", industryRids);
        List<String> objectTypeIds = new ArrayList<>();
        InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();

        for (ObjectBatchBean batchBean : objectBatchBeans) {
            try {
                log.info("object type bean: {}", objectMapper.writeValueAsString(batchBean));
                if (StringUtils.isBlank(batchBean.getObjectTypeId())) {
                    log.error("object type id is blank");
                    continue;
                }
                String rid = isObjectTypeExists(cds, batchBean.getObjectTypeId());
                if (StringUtils.isNotBlank(rid)) {
                    log.info("object type of {} is already exists", batchBean.getObjectTypeId());
                    objectTypeIds.add(rid);
                    continue;
                }

                InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
                objectTypeVO.setObjectTypeName(batchBean.getName());
                objectTypeVO.setObjectTypeDesc(batchBean.getObjectTypeId());

                InfoObjectDef infoObjectDef = infoObjectDefs.addRootInfoObjectDef(objectTypeVO);
                if (infoObjectDef != null) {
                    objectTypeIds.add(infoObjectDef.getObjectTypeName());
                    String industryRid = industryRids.get(batchBean.getIndustryIndex());
                    if (StringUtils.isNotBlank(industryRid)) {
                        infoObjectDef.linkIndustryType(industryRid);
                    } else {
                        log.error("industry type of index {} is not exists, object type of {} should be linked to {}", batchBean.getIndustryIndex(), batchBean.getObjectTypeId(), InfoObjectTypeDefService.UNDEFINED_INDUSTRY);
                        IndustryTypeVO defaultIndustry = IndustryTypeFeatures.getIndustryTypeByName(CimConstants.defauleSpaceName, InfoObjectTypeDefService.UNDEFINED_INDUSTRY);
                        if (defaultIndustry != null) {
                            infoObjectDef.linkIndustryType(defaultIndustry.getIndustryTypeId());
                        } else {
                            log.error("industry type of {} not exists", InfoObjectTypeDefService.UNDEFINED_INDUSTRY);
                        }
                    }
                } else {
                    log.error("object type of {} add failed", batchBean.getObjectTypeId());
                }
            } catch (DataServiceModelRuntimeException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return objectTypeIds;
    }

    /**
     * 获取指定对象关联的所有属性集名称
     *
     * @param cds
     * @param objectTypeId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public static Map<String, String> getAllSelfDataSetNameByObject(CimDataSpace cds, String objectTypeId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        InformationExplorer ie = cds.getInformationExplorer();
        ExploreParameters ep = new ExploreParameters();
        ep.setType(BusinessLogicConstant.INFOOBJECTTYPE_DATASET_MAPPING_FACT_TYPE_NAME);
        ep.setDefaultFilteringItem(new EqualFilteringItem("infoObjectTypeName", objectTypeId));
        List<Fact> linkedMappingList = ie.discoverFacts(ep);
        Map<String, String> dataSetNames = new HashMap<>();
        if (linkedMappingList != null) {
            for (Fact fact : linkedMappingList) {
                String datasetId = fact.getProperty("datasetId").getPropertyValue().toString();
                if (StringUtils.isNotBlank(datasetId)) {
                    Fact dataSetFact = cds.getFactById(datasetId);
                    if (dataSetFact != null) {
                        dataSetNames.put(dataSetFact.getProperty(BusinessLogicConstant.DATASET_FACT_FIELDNAME_NAME).getPropertyValue().toString(), dataSetFact.getId());
                    } else {
                        log.error("data set of {} not exists", datasetId);
                    }
                } else {
                    log.error("status record fact of {} do not have datasetId property", fact.getId());
                }
            }
        }
        return dataSetNames;
    }

    public static Map<Integer, String> addDataSetBatch(CIMModelCore modelCore, CimDataSpace cds, List<DataSetBatchBean> dataSetBatchBeans) { //NOSONAR
        Map<Integer, String> dataSetRids = new HashMap<>();
        if (dataSetBatchBeans != null) {

            DatasetDefs datasetDefs = modelCore.getDatasetDefs();
            for (DataSetBatchBean batchBean : dataSetBatchBeans) {
                try {
                    log.info("data set bean: {}", objectMapper.writeValueAsString(batchBean));
                    DatasetVO datasetVO = new DatasetVO();
                    datasetVO.setDatasetName(batchBean.getName());
                    datasetVO.setDatasetDesc(batchBean.getName());
                    datasetVO.setDatasetClassify(batchBean.getDataType());

                    switch (batchBean.getDataSetType()) {
                        case "实例属性集":
                            datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
                            break;
                        case "类型属性集":
                            datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.OBJECT);
                            break;
                        default:
                            log.error("data set type of {} is not support", batchBean.getDataSetType());
                    }

                    switch (batchBean.getDataStructure()) {
                        case "一般属性集":
                            datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);
                            break;
                        case "集合型属性集":
                            datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.COLLECTION);
                            break;
                        default:
                            log.error("data set structure of {} is not support", batchBean.getDataStructure());
                    }

                    if (datasetVO.getDataSetType() == null || datasetVO.getDatasetStructure() == null) {
                        continue;
                    } else {
                        String objectTypeId = batchBean.getObjectTypeId();
                        Map<String, String> dataSetNames = getAllSelfDataSetNameByObject(cds, objectTypeId);
                        if (dataSetNames.containsKey(batchBean.getName())) {
                            log.error("object type of {} already contain data set of {}", objectTypeId, batchBean.getName());
                            dataSetRids.put(batchBean.getIndex(), dataSetNames.get(datasetVO.getDatasetName()));
                        } else {
                            DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
                            if (datasetDef != null) {
                                dataSetRids.put(batchBean.getIndex(), datasetDef.getDatasetRID());
                                log.info("add data set of {} to object type of {}", datasetDef.getDatasetRID(), objectTypeId);
                                InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
                                infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
                            } else {
                                log.error("data set of {} add failed", batchBean.getIndex());
                            }
                        }
                    }
                } catch (DataServiceModelRuntimeException e) {
                    e.printStackTrace();
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataSetRids;
    }

    public static List<String> getAllPropertyNameByDataSet(CimDataSpace cds, String dataSetRid) throws CimDataEngineRuntimeException {
        List<String> propertyNames = new ArrayList<>();
        Fact datasetFact = cds.getFactById(dataSetRid);
        List<Relation> propertyTypeLinkRelationList = datasetFact.getAllSpecifiedRelations(BusinessLogicConstant.CONTAINS_PROPERTY_TYPE_RELATION_TYPE_NAME, RelationDirection.FROM);
        if (propertyTypeLinkRelationList != null) {
            for (Relation currentRelation : propertyTypeLinkRelationList) {
                Relationable propertyTypeFact = currentRelation.getToRelationable();
                Property property = propertyTypeFact.getProperty(BusinessLogicConstant.PROPERTY_TYPE_FACT_FIELDNAME_TYPENAME);
                if (property != null) {
                    propertyNames.add(property.getPropertyValue().toString());
                }
            }
        }
        return propertyNames;
    }

    public static Map<Integer, String> addPropertiesBatch(CIMModelCore modelCore, CimDataSpace cds, List<PropertyBatchBean> propertyBatchBeans, Map<Integer, String> dataSetIds) { //NOSONAR
        log.info("data set index and rid map: {}", dataSetIds);
        Map<Integer, String> propertyIds = new HashMap<>();
        if (propertyBatchBeans != null && propertyBatchBeans.size() > 0) {
            PropertyTypeDefs propertyTypeDefs = modelCore.getPropertyTypeDefs();
            for (PropertyBatchBean batchBean : propertyBatchBeans) {
                try {
                    log.info("property bean: {}", objectMapper.writeValueAsString(batchBean));
                    PropertyTypeVO typeVO = new PropertyTypeVO();
                    typeVO.setPropertyTypeName(batchBean.getName());
                    typeVO.setPropertyTypeDesc(batchBean.getAlias());
                    typeVO.setPropertyFieldDataClassify(dataTypeMap.get(batchBean.getType()));
                    // String dataSetId = dataSetIds.get(batchBean.getIndex());
                    String dataSetId = dataSetIds.get(batchBean.getDataSetIndex());
                    List<String> propertyNames = getAllPropertyNameByDataSet(cds, dataSetId);
                    log.info("data set of {} contains property: {}", objectMapper.writeValueAsString(propertyNames));
                    if (propertyNames.contains(batchBean.getName())) {
                        log.info("data set of {} is already contain property of {}", dataSetId, batchBean.getName());
                    } else {
                        PropertyTypeDef propertyTypeDef = propertyTypeDefs.addPropertyTypeDef(typeVO);
                        if (propertyTypeDef != null && StringUtils.isNotBlank(dataSetId)) {
                            DatasetDef datasetDef = modelCore.getDatasetDefByRID(dataSetId);
                            if (datasetDef == null) {
                                log.error("data set of {} not exists", dataSetIds.get(batchBean.getDataSetIndex()));
                            } else {
                                datasetDef.addPropertyTypeDef(propertyTypeDef);
                                propertyIds.put(batchBean.getIndex(), propertyTypeDef.getPropertyTypeRID());

                                PropertyTypeRestrictVO restrictVO = new PropertyTypeRestrictVO();
                                restrictVO.setNull(batchBean.getIsNull());
                                restrictVO.setPrimaryKey(batchBean.getIsPrimary());
                                restrictVO.setDefaultValue(batchBean.getDefaultValue());
                                restrictVO.setPropertyTypeId(propertyTypeDef.getPropertyTypeRID());
                                // datasetDef.addPropertyTypeRestrict(restrictVO);
                                PropertyTypeRestrictFeatures.addRestrictToDataSet(CimConstants.defauleSpaceName, datasetDef.getDatasetRID(), restrictVO);
                            }
                        } else {
                            log.error("property of {} add failed or data set index is not provided", batchBean.getIndex());
                        }
                    }
                } catch (DataServiceModelRuntimeException e) {
                    e.printStackTrace();
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.error("property is null");
        }

        return propertyIds;
    }


}
