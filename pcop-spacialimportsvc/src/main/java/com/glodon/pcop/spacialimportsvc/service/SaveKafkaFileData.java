package com.glodon.pcop.spacialimportsvc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.KafkaFileDataBean;
import com.glodon.pcop.cim.common.model.KafkaGisDataBean;
import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Property;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.core.tenancy.context.TenantContext;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import com.glodon.pcop.spacialimportsvc.config.OrientdbPropertyConfig;
import com.glodon.pcop.spacialimportsvc.exception.InfoObjectNotFoundException;
import com.glodon.pcop.spacialimportsvc.model.FilePropertyMappingBean;
import com.glodon.pcop.spacialimportsvc.util.DateUtil;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SaveKafkaFileData {
    private static Logger log = LoggerFactory.getLogger(SaveKafkaFileData.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrientdbPropertyConfig orientdbConfig;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobInfoClient jobInfoClient;
    @Autowired
    private TenantContext tenantContext;

    //    private static final String OBJECT_AND_PROPERTIES_CACHE = "OBJECT_AND_PROPERTIES_CACHE";
    private static final String OBJECT_AND_PROPERTIES_CACHE = "CIM_BUILDIN_CLUSTER_RESOURCE_POOL_V1D0";

    CacheManager cacheManager = null;
    Cache<String, List> objectAndPropertiesCache = null;

    @PostConstruct
    public void cacheInit() {
        log.info("===initialize object and properties on-heap cache...");
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .build(true);

        CacheConfiguration<String, List> cacheConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, List.class,
                        ResourcePoolsBuilder.heap(100))
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(3)))
                        .build();

        objectAndPropertiesCache = cacheManager.createCache(OBJECT_AND_PROPERTIES_CACHE, cacheConfiguration);
    }

    @PreDestroy
    public void cacheDestroy() {
        if (cacheManager != null) {
            cacheManager.close();
        }
        log.info("===close initialize object and properties on-heap cache!!!");
    }

    private static final String ORIENT_CONFIGURATION = "orient configuration: location={}, spaceName={}";

    public static String defaultTenantId = "2";

    private Map<String, FilePropertyMappingBean> propertyMappingBeanMap = new HashMap<>();

    private static Map<String, List<PropertyTypeVO>> objPropertieMap = new HashMap<>();

    private static final String CIM_DATA_IMPORT_END = "cim入库完成";
    private static final String CIM_DATA_IMPORT_FAILED = "cim入库失败";

    private Map<String, Set<String>> mappingStringTranslater(String mappingStr) throws IOException {
        Map<String, Set<String>> reMap = new HashMap<>();
        List<PropertyMappingInputBean> metdataBeans = objectMapper.readValue(mappingStr,
                new TypeReference<List<PropertyMappingInputBean>>() {
                });

        for (PropertyMappingInputBean bean : metdataBeans) {
            Set<String> desPros = new HashSet<>();
            if (reMap.containsKey(bean.getSrcPropertyName())) {
                desPros = reMap.get(bean.getSrcPropertyName());
            }
            desPros.add(bean.getDesPropertyName());
            reMap.put(bean.getSrcPropertyName(), desPros);
        }
        return reMap;
    }

    /**
     * 根据taskId查询相应的mapping信息
     *
     * @param taskId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     * @throws IOException
     */
    public FilePropertyMappingBean getMappingInfoByTaskId(String taskId) throws CimDataEngineRuntimeException,
                                                                                CimDataEngineInfoExploreException,
                                                                                IOException {
        FilePropertyMappingBean mappingBean = new FilePropertyMappingBean();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(orientdbConfig.getLocation(),
                    orientdbConfig.getDiscoverSpace(), orientdbConfig.getAccount(), orientdbConfig.getPassword());
            log.debug(ORIENT_CONFIGURATION, orientdbConfig.getLocation(), orientdbConfig.getDiscoverSpace());

            FilteringItem item = new EqualFilteringItem(CimConstants.FileImportProperties.TASK_ID, taskId);
            ExploreParameters exploreParameters = new ExploreParameters();
            exploreParameters.setDefaultFilteringItem(item);
            exploreParameters.setType(CimConstants.FileImportProperties.DATA_IMPORT_MAPPING_INFO_TYPE_NAME);
            log.debug("explore parameters: {}", objectMapper.writeValueAsString(exploreParameters));
            InformationExplorer ip = cds.getInformationExplorer();
            List<Fact> factList = ip.discoverInheritFacts(exploreParameters);
            log.info("fact list size: {}", factList.size());
            if (factList != null && factList.size() > 0) {
                Fact fact = factList.get(0);
                mappingBean.setId(fact.getProperty("ID").getPropertyValue().toString());
                mappingBean.setTaskId(
                        fact.getProperty(CimConstants.FileImportProperties.TASK_ID).getPropertyValue().toString());
                mappingBean.setTenantId(
                        fact.getProperty(CimConstants.FileImportProperties.TENANT_ID).getPropertyValue().toString());
                Property datasetIdProperty = fact.getProperty(CimConstants.FileImportProperties.DATA_SET_ID);
                if (datasetIdProperty != null) {
                    Object pm = datasetIdProperty.getPropertyValue();
                    log.info("object type: {}", pm.getClass());
                    if (pm instanceof OrientVertex) {
                        mappingBean.setDataSetId(((OrientVertex) pm).getId().toString());
                    } else {
                        mappingBean.setDataSetId(fact.getProperty(
                                CimConstants.FileImportProperties.DATA_SET_ID).getPropertyValue().toString());
                    }
                }
                Property datasetNameProperty = fact.getProperty(CimConstants.FileImportProperties.DATA_SET_NAME);
                if (datasetNameProperty != null) {
                    mappingBean.setDataSetName(datasetNameProperty.getPropertyValue().toString());

                }
                Property objectTypeIdProperty = fact.getProperty(CimConstants.FileImportProperties.OBJECT_TYPE_ID);
                if (objectTypeIdProperty != null) {
                    mappingBean.setObjectTypeId(objectTypeIdProperty.getPropertyValue().toString());
                }
                log.info("propertiesMapping: {}",
                        fact.getProperty(
                                CimConstants.FileImportProperties.PROPERTIES_MAPPING).getPropertyValue().toString());
                mappingBean.setPropertyMapping(mappingStringTranslater(fact.getProperty(
                        CimConstants.FileImportProperties.PROPERTIES_MAPPING).getPropertyValue().toString()));
            } else {
                log.error("mapping info of taskId={} not found", taskId);
                mappingBean = null;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return mappingBean;
    }

    public boolean saveMessageData(String message) {//NOSONAR
        boolean flag = false;
        CimDataSpace cds = null;
        String tenantId = null;
        KafkaFileDataBean kfData = null;
        try {
            log.debug(ORIENT_CONFIGURATION, orientdbConfig.getLocation(), orientdbConfig.getDiscoverSpace());
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(orientdbConfig.getLocation(),
                    orientdbConfig.getDiscoverSpace(), orientdbConfig.getAccount(), orientdbConfig.getPassword());

            kfData = objectMapper.readValue(message, KafkaFileDataBean.class);

            FilePropertyMappingBean mappingBean = null;

            if (StringUtils.isNotBlank(kfData.getTaskId())) {
                if (propertyMappingBeanMap.containsKey(kfData.getTaskId())) {
                    mappingBean = propertyMappingBeanMap.get(kfData.getTaskId());
                } else {
                    mappingBean = getMappingInfoByTaskId(kfData.getTaskId());
                    if (mappingBean != null) {
                        propertyMappingBeanMap.put(kfData.getTaskId(), mappingBean);
                    } else {
                        log.error("no mapping info");
                        return flag;
                    }
                }

                if (StringUtils.isNotBlank(mappingBean.getTenantId())) {
                    tenantId = mappingBean.getTenantId();
                    log.debug("task id={}, tennat id={} is used", mappingBean.getTaskId(), mappingBean.getTenantId());
                } else {
                    tenantId = defaultTenantId;
                    log.info("task id={}, tenant id is balnk, default tennat id={} is used", mappingBean.getTaskId(),
                            defaultTenantId);
                }

                if (cds.hasInheritFactType(mappingBean.getObjectTypeId())) {
                    InfoObjectDef infoObjectDef = new InfoObjectDefDSImpl(ImportCimConstants.defauleSpaceName,
                            tenantId, mappingBean.getObjectTypeId());
                    ((InfoObjectDefDSImpl) infoObjectDef).setCimDataSpace(cds);

                    Map<String, String> srcData = kfData.getData();
                    Map<String, Set<String>> propertyMapping = mappingBean.getPropertyMapping();
                    if (srcData != null && srcData.size() > 0 && propertyMapping != null && propertyMapping.size() > 0) {
                        Map<String, Object> baseInfo = new HashMap<>();
                        Map<String, Object> generalInfo = new HashMap<>();
                        for (Map.Entry<String, String> entry : srcData.entrySet()) {
                            if (StringUtils.isNotBlank(entry.getKey())) {
                                //ID 单独处理：objectTypeId_ID
                                if (CimConstants.ID_PROPERTY_TYPE_NAME.equals(entry.getKey().trim().toUpperCase())) {
                                    baseInfo.put(entry.getKey(), entry.getValue());
                                    // baseInfo.put(entry.getKey(), mappingBean.getObjectTypeId() + "_" + entry
                                    // .getValue());
                                }
                                //one to many
                                Set<String> desKeies = propertyMapping.get(entry.getKey());
                                if (desKeies == null || desKeies.size() < 1) {
                                    log.debug("not mapping key: {}", entry.getKey());
                                    continue;
                                }
                                for (String desKey : desKeies) {
                                    if (StringUtils.isNotBlank(desKey)) {
                                        if (CimConstants.BASE_DATASET_KEYS_SET.contains(desKey)) {
                                            baseInfo.put(desKey, entry.getValue());
                                        } else {
                                            generalInfo.put(desKey, entry.getValue());
                                        }
                                    } else {
                                        log.debug("not mapping key: {}", entry.getKey());
                                    }
                                }
                            } else {
                                log.error("input data key is blank");
                            }
                        }
                        if (generalInfo.size() > 0) {
                            InfoObjectValue objectValue = new InfoObjectValue();
                            objectValue.setBaseDatasetPropertiesValue(baseInfo);
                            objectValue.setGeneralDatasetsPropertiesValue(generalInfo);
                            InfoObject infoObject = infoObjectDef.newObject(objectValue, false);
                            if (infoObject != null) {
                                flag = true;
                            } else {
                                log.error("save failed: {}", message);
                            }
                        } else {
                            log.error("only ID is not blank");
                        }
                    } else {
                        log.error("source data is empty");
                    }
                } else {
                    log.error(ORIENT_CONFIGURATION, orientdbConfig.getLocation(), orientdbConfig.getDiscoverSpace());
                    log.error("object type of {} not found", mappingBean.getObjectTypeId());
                }
            } else {
                log.error("kafka message taskId is blank");
            }

            if (kfData == null) {
                log.error("kfData is null");
            } else {
                log.debug("taskId={}", kfData.getTaskId());
                Map<String, String> data = kfData.getData();
                //如果map的key包含success证明消费已完成 文件随即完成
                if (data.containsKey(CimConstants.JOB_END_IDENTIFIER)) {
                    //更改文件任务状态kfData.getTaskId()
                    tenantContext.setTenantId(Long.valueOf(tenantId));
                    sendTaskStatusMq(kfData.getTaskId(), JobStatusEnum.ENDED.getCode());
                    log.info(" @@@@@@@@@@@@@@ ENDED TIME{}", DateUtil.getCurrentDate());
                }
            }
        } catch (Exception e) {
            log.error("save kafka data failed e is {} ", e);
            if (tenantId != null) {
                sendTaskStatusMq(kfData.getTaskId(), JobStatusEnum.FAIL.getCode());
                log.error("failed{}", e.getMessage());
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return flag;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 发送更改任务消息到MQ--更改文件任务状态
     *
     * @return
     */
    public void sendTaskStatusMq(String taskId, String status, String... statusName) {
        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus(status);
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        if (statusName != null) {
            jobParmDTO.setStatusName(statusName[0]);
        }
        log.info("更改文件任务状态结束{}", DateUtil.getCurrentDate());
        jobInfoClient.updateStatus(Long.valueOf(taskId), jobParmDTO);
    }


    public void saveGisData(CimDataSpace cds, String message) throws DataServiceUserException,
                                                                     CimDataEngineRuntimeException,
                                                                     CimDataEngineInfoExploreException {
        KafkaGisDataBean dataBean = JSON.parseObject(message, KafkaGisDataBean.class);
        log.debug("message bean: {}", dataBean);
        String objectTypeId = dataBean.getObjectName();
        String tenantId = dataBean.getTenantId();
        Boolean isUpdate = dataBean.getUpdate();
        Map<String, Object> data = dataBean.getData();
        Assert.hasText(objectTypeId, "object type is mandatory");
        if (StringUtils.isBlank(tenantId)) {
            tenantId = BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME;
            log.debug("tenant id is blank, default tenant is used: [{}]",
                    BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME);
        }
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName,
                tenantId);
        modelCore.setCimDataSpace(cds);
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);

        if (data != null && data.size() > 0) {
            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(data);
            if (!isUpdate) {
                InfoObject infoObject = infoObjectDef.newObject(objectValue, false);
                if (infoObject == null || StringUtils.isBlank(infoObject.getObjectInstanceRID())) {
                    log.error("add fact failed: [{}]", message);
                }
            } else {
                Object id = data.get(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME);
                if (id != null && StringUtils.isNotBlank(id.toString())) {
                    Fact fact = getFactByID(modelCore.getCimDataSpace(), objectTypeId, id.toString());
                    InfoObject infoObject;
                    if (fact != null) {
                        objectValue.setObjectInstanceRID(fact.getId());
                        infoObject = infoObjectDef.updateObject(objectValue);
                    } else {
                        infoObject = infoObjectDef.newObject(objectValue, false);
                    }
                    if (infoObject == null || StringUtils.isBlank(infoObject.getObjectInstanceRID())) {
                        log.error("update fact failed: [{}]", message);
                    }
                } else {
                    log.error("update fact failed, ID is missing: [{}]", message);
                }
            }
        }
    }

    public Fact getFactByID(CimDataSpace cds, String objectTypeId, String id) throws CimDataEngineRuntimeException,
                                                                                     CimDataEngineInfoExploreException {
        Assert.hasText(id, "ID is mandatory");
        FilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME, id);
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);
        ep.setType(objectTypeId);

        InformationExplorer ie = cds.getInformationExplorer();
        List<Fact> factList = ie.discoverInheritFacts(ep);

        if (factList.size() > 0) {
            return factList.get(0);//id is unique
        } else {
            return null;
        }
    }


    public void createOrUpdateGisData(CimDataSpace cds, String message) throws DataServiceUserException,
                                                                               CimDataEngineRuntimeException,
                                                                               CimDataEngineInfoExploreException,
                                                                               InfoObjectNotFoundException {
        KafkaGisDataBean dataBean = JSON.parseObject(message, KafkaGisDataBean.class);
        log.debug("message bean: {}", dataBean);
        String objectTypeId = dataBean.getObjectName();
        String tenantId = dataBean.getTenantId();
        Boolean isUpdate = dataBean.getUpdate();
        Map<String, Object> data = dataBean.getData();
        //Assert.hasText(objectTypeId, "object type is mandatory");
        if (dataBean.getJobEnd() != null && dataBean.getJobEnd().equals(true)) {
            tenantContext.setTenantId(Long.valueOf(tenantId));
            // sendTaskStatusMq(dataBean.getTaskId().toString(), JobStatusEnum.ENDED.getCode());
            sendTaskStatusMq(dataBean.getTaskId().toString(), JobStatusEnum.ENDED.getCode(), CIM_DATA_IMPORT_END);
            log.info(" @@@@@@@@@@@@@@ ENDED TIME{}", DateUtil.getCurrentDate());
            return;
        }
        if (StringUtils.isBlank(tenantId)) {
            tenantId = BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME;
            log.debug("tenant id is blank, default tenant is used: [{}]",
                    BusinessLogicConstant.PUBLIC_TENANT_DIMENSION_NAME);
        }
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName,
                tenantId);
        modelCore.setCimDataSpace(cds);
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);

        if (data != null && data.size() > 0) {
            if (data.containsKey(CimConstants.OrientDBReservedKeys.ID)) {
                data.put(CimConstants.OrientDBReservedKeys.ID_, data.get(CimConstants.OrientDBReservedKeys.ID));
                data.remove(CimConstants.OrientDBReservedKeys.ID);
            }
            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(data);
            if (!isUpdate) {
                Fact fact = addInstanceByMessage(tenantId, cds, dataBean);
                if (fact == null) {
                    log.error("add fact failed: [{}]", message);
                }
            } else {
                Object id = data.get(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME);
                if (id != null && StringUtils.isNotBlank(id.toString())) {
                    Fact fact = getFactByID(modelCore.getCimDataSpace(), objectTypeId, id.toString());
                    InfoObject infoObject;
                    if (fact != null) {
                        objectValue.setObjectInstanceRID(fact.getId());
                        infoObject = infoObjectDef.updateObject(objectValue);
                    } else {
                        infoObject = infoObjectDef.newObject(objectValue, false);
                    }
                    if (infoObject == null || StringUtils.isBlank(infoObject.getObjectInstanceRID())) {
                        log.error("update fact failed: [{}]", message);
                    }
                } else {
                    log.error("update fact failed, ID is missing: [{}]", message);
                }
            }
        }
    }


    public Fact addInstanceByMessage(String tenantId, CimDataSpace cds, KafkaGisDataBean dataBean)
            throws CimDataEngineRuntimeException,
                   CimDataEngineInfoExploreException, InfoObjectNotFoundException {
        // if (!objPropertieMap.containsKey(dataBean.getObjectName())) {
        if (!objectAndPropertiesCache.containsKey(dataBean.getObjectName())) {
            InfoObjectTypeVO targetObjectType = InfoObjectFeatures.getInfoObjectTypeVOByType(cds,
                    dataBean.getObjectName(), true, true);

            int tryTimes = 0;
            while (targetObjectType == null && tryTimes < 3) {
                tryTimes++;
                log.info("try to retrieve object type: [{}]", tryTimes);
                cds.flushUncommitedData();
                try {
                    Thread.sleep(tryTimes * 10 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                targetObjectType = InfoObjectFeatures.getInfoObjectTypeVOByType(cds,
                        dataBean.getObjectName(), true, true);
            }
            if (targetObjectType == null) {
                throw new InfoObjectNotFoundException();
            }
            List<DatasetVO> linkedDataset = targetObjectType.getLinkedDatasets();
//            List<DatasetVO> linkedDataset = InfoObjectFeatures.getLinkedDatasets(cds, dataBean.getObjectName(), true);
            if (linkedDataset == null) {
                linkedDataset = new ArrayList<>();
            }
            DatasetVO baseDatasetVO = DatasetFeatures.getBaseDatasetVO(cds);
            if (baseDatasetVO != null) {
                linkedDataset.add(baseDatasetVO);
            }
            List<PropertyTypeVO> tmpPropertyTypeVOList =
                    InfoObjectFeatures.mergePropertyTypesFromDatasets(linkedDataset, false);
            if (tmpPropertyTypeVOList == null) {
                tmpPropertyTypeVOList = new ArrayList<>();
            }
            // objPropertieMap.put(dataBean.getObjectName(), tmpPropertyTypeVOList);
            log.info("initialize properties of [{}]", dataBean.getObjectName());
            objectAndPropertiesCache.put(dataBean.getObjectName(), tmpPropertyTypeVOList);
        }

        // List<PropertyTypeVO> propertyTypeVOList = objPropertieMap.get(dataBean.getObjectName());
        List<PropertyTypeVO> propertyTypeVOList = objectAndPropertiesCache.get(dataBean.getObjectName());
        Fact currentFactData = CimDataEngineComponentFactory.createFact(dataBean.getObjectName());
        CommonOperationUtil.setInitFactPropertiesValue(dataBean.getData(), propertyTypeVOList, currentFactData);
        if (currentFactData.getInitProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME) == null) {
            currentFactData.setInitProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME,
                    CommonOperationUtil.generateObjectInstanceUUID(dataBean.getObjectName()));
        }
        Fact loadResultFact = cds.addFact(currentFactData);
        if (loadResultFact != null) {
            CommonOperationUtil.addToBelongingTenant(cds, tenantId, loadResultFact);
        }
        return loadResultFact;
    }


}
