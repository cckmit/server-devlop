package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.BimfaceVO;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.InValueFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DataInstance;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.Dataset;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.BatchDataOperationResultDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.DataInstanceRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectWithMergedDatasetProperties;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectWithMergedDatasetPropertiesRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.ExternalConfigFilesReader;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.OutputQueryFroNameBean;
import com.glodon.pcop.cimsvc.model.OutputQueryFroNameVO;
import com.glodon.pcop.cimsvc.model.v2.*;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import com.glodon.pcop.cimsvc.util.RIDUtil;
import com.glodon.pcop.cimsvc.util.condition.QueryConditionParser;
import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import jnr.ffi.annotations.In;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.query.JoinType;
import org.apache.metamodel.query.OperatorType;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class InstancesService {
    private static Logger log = LoggerFactory.getLogger(InstancesService.class);

    private static final String DATA_SET_NOT_FOUND = "DataSet=%s of ObjectType=%s not found!";

    @Value("${my.bimface.offlinePkgPath}")
    private String offlinePkgPath;

    private Connection connection;

    /**
     * 查询实例数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetName  返回属于该属性集的数据，若为空则返回所有single数据
     * @param queryInput   分页属性非空，查询条件为空则返回所有数据
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public SingleQueryOutput queryInstanceSingle(String tenantId, String objectTypeId, String dataSetName,
                                                 InstancesQueryInput queryInput) throws InputErrorException {//NOSONAR
        SingleQueryOutput resultQueryOutput = new SingleQueryOutput();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            ExploreParameters exploreParameters = QueryConditionParser.parserQueryInput(cds, objectTypeId, queryInput
                    , false);
            cimModelCore.setCimDataSpace(cds);
            log.info("query condition input: {}", JSON.toJSONString(exploreParameters));

            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
            if (infoObjectDef == null) {
                log.error("object type of {} not exists or not belong to tenant of {}", objectTypeId, tenantId);
                return resultQueryOutput;
            }
            InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);

            List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
            log.info("Query instance count {}", infoObjectList.size());
            List<SingleInstancesQueryOutput> queryResults = new ArrayList<>();
            Long totalCount = 0L;
            List<DatasetVO> linkedDatasets = InfoObjectFeatures.getLinkedDatasets(cds, objectTypeId, true);
            if (infoObjectList != null && infoObjectList.size() > 0) {
                for (InfoObject infoObject : infoObjectList) {
                    SingleInstancesQueryOutput queryOutput = new SingleInstancesQueryOutput();
                    queryOutput.setInstanceRid(infoObject.getObjectInstanceRID());
                    ((InfoObjectDSImpl) infoObject).setLinkedDatasets(linkedDatasets);
                    if (StringUtils.isBlank(dataSetName)) {
                        Map<String, Map<String, Object>> tmpMap = infoObject.getObjectPropertiesByDatasets();
                        if (tmpMap == null) {
                            tmpMap = new HashMap<>();
                        }
                        tmpMap.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
                        queryOutput.setInstanceData(tmpMap);
                    } else {
                        Map<String, Map<String, Object>> tmpMap = new HashMap<>();
                        tmpMap.put(dataSetName, infoObject.getObjectPropertiesByDataset(dataSetName));
                        tmpMap.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
                        queryOutput.setInstanceData(tmpMap);
                    }
                    queryResults.add(queryOutput);
                }
                totalCount = ((InfoObjectDefDSImpl) infoObjectDef).countObjects(exploreParameters);
            }
            resultQueryOutput.setTotalCount(totalCount);
            resultQueryOutput.setInstances(queryResults);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return resultQueryOutput;
    }

    /**
     * 新增单条实例数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectValueList
     * @param isAddRelation
     * @return
     */
    public BatchDataOperationResult addInstanceSingle(String tenantId, String objectTypeId, List<InfoObjectValue> objectValueList, boolean isAddRelation) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        BatchDataOperationResult dataOperationResult = infoObjectDef.newObjects(objectValueList, isAddRelation);
        return dataOperationResult;
    }

    public BatchDataOperationResult updateInstanceSingle(String tenantId, String objectTypeId,
                                                         List<Map<String, Object>> objectValueList) {
        BatchDataOperationResult dataOperationResult = null;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
            int failedItems = updateSingleObjectValuesFormal(cds, objectTypeId, objectValueList, infoObjectValueList);
            dataOperationResult = infoObjectDef.updateObjects(infoObjectValueList);
            dataOperationResult.getOperationStatistics().setFailItemsCount(dataOperationResult.getOperationStatistics().getFailItemsCount() + failedItems);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return dataOperationResult;
    }

    /**
     * 根据rid或者id删除实例
     *
     * @param tenantId
     * @param objectTypeId
     * @param instanceRids
     * @return
     */
    public BatchDataOperationResult deleteInstanceSingle(String tenantId, String objectTypeId,
                                                         List<String> instanceRids) {
        BatchDataOperationResult dataOperationResult = null;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            // modelCore.setCimDataSpace(cds);
            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);

            List<String> ridList = new ArrayList<>();
            List<Object> idList = new ArrayList<>();
            for (String rid : instanceRids) {
                if (RIDUtil.isAvailableOrientdbRid(rid)) {
                    ridList.add(rid);
                } else {
                    idList.add(rid);
                }
            }
            int idNotFoundItems = idList.size();
            if (idList.size() > 0) {
                List<Fact> factList = queryInheritFactById(cds, objectTypeId, idList);
                if (factList != null && factList.size() > 0) {
                    idNotFoundItems = idList.size() - factList.size();
                    for (Fact fact : factList) {
                        ridList.add(fact.getId());
                    }
                }
            }
            dataOperationResult = infoObjectDef.deleteObjects(ridList);
            dataOperationResult.getOperationStatistics().setFailItemsCount(dataOperationResult.getOperationStatistics().getFailItemsCount() + idNotFoundItems);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return dataOperationResult;
    }

    /**
     * 解析查询条件
     *
     * @param objectTypeId
     * @param queryInput
     * @return
     */
    private static ExploreParameters parserQueryInput(String objectTypeId, InstancesQueryInput queryInput,
                                                      boolean isIncludeCollectionDataSet) {//NOSONAR
        ExploreParameters exploreParameters = null;
        if (queryInput != null) {
            exploreParameters = new ExploreParameters();
            if (queryInput.getStartPage() < 1) {
                exploreParameters.setStartPage(1);
            } else {
                exploreParameters.setStartPage(queryInput.getStartPage());
            }

            if (queryInput.getEndPage() < 1) {
                exploreParameters.setEndPage(2);
            } else {
                exploreParameters.setEndPage(queryInput.getEndPage());
            }

            if (queryInput.getPageSize() < 1) {
                exploreParameters.setPageSize(50);
            } else {
                exploreParameters.setPageSize(queryInput.getPageSize());
            }

            List<CommonQueryConditionsBean> queryConditionsBeanList = queryInput.getConditions();
            if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
                for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                    FilteringItem filteringItem = QueryConditionsUtil.parseCommonQueryCondition(objectTypeId,
                            queryConditionsBeanList.get(i), isIncludeCollectionDataSet);
                    if (i == 0) {
                        exploreParameters.setDefaultFilteringItem(filteringItem);
                    } else {
                        exploreParameters.addFilteringItem(filteringItem,
                                queryConditionsBeanList.get(i).getFilterLogical());
                    }
                }
            }

            if (queryInput.getSortAttributes() != null && queryInput.getSortAttributes().size() > 0 && queryInput.getSortingLogic() != null) {
                exploreParameters.setSortAttributes(queryInput.getSortAttributes());
                exploreParameters.setSortingLogic(queryInput.getSortingLogic());
            }
        }
        return exploreParameters;
    }


    private static ExploreParameters parserStringQueryInput(InstancesQueryInput queryInput) {//NOSONAR
        ExploreParameters exploreParameters = null;
        if (queryInput != null) {
            exploreParameters = new ExploreParameters();
            if (queryInput.getStartPage() < 1) {
                exploreParameters.setStartPage(1);
            } else {
                exploreParameters.setStartPage(queryInput.getStartPage());
            }

            if (queryInput.getEndPage() < 1) {
                exploreParameters.setEndPage(2);
            } else {
                exploreParameters.setEndPage(queryInput.getEndPage());
            }

            if (queryInput.getPageSize() < 1) {
                exploreParameters.setPageSize(50);
            } else {
                exploreParameters.setPageSize(queryInput.getPageSize());
            }

            List<CommonQueryConditionsBean> queryConditionsBeanList = queryInput.getConditions();
            if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
                for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                    FilteringItem filteringItem =
                            QueryConditionsUtil.parseStringQueryCondition(queryConditionsBeanList.get(i));
                    if (i == 0) {
                        exploreParameters.setDefaultFilteringItem(filteringItem);
                    } else {
                        exploreParameters.addFilteringItem(filteringItem,
                                queryConditionsBeanList.get(i).getFilterLogical());
                    }
                }
            }

            if (queryInput.getSortAttributes() != null && queryInput.getSortAttributes().size() > 0 && queryInput.getSortingLogic() != null) {
                exploreParameters.setSortAttributes(queryInput.getSortAttributes());
                exploreParameters.setSortingLogic(queryInput.getSortingLogic());
            }
        }
        return exploreParameters;
    }


    /**
     * 查询集合数据集数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetName
     * @param queryInput
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public CollectionQueryOutput queryInstanceCollection(String tenantId, String objectTypeId, String instanceRid,
                                                         String dataSetName, InstancesQueryInput queryInput) throws DataServiceModelRuntimeException, EntityNotFoundException {//NOSONAR
        // List<Map<String, Object>> queryResult = new ArrayList<>();
        List<CollectionInstancesQueryOutput> queryResults = new ArrayList<>();
        Long totalCount = 0L;
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        ExploreParameters exploreParameters = parserQueryInput(objectTypeId, queryInput, true);
        if (StringUtils.isNotBlank(dataSetName)) {
            Dataset dataset = infoObjectDef.getCollectionDataset(dataSetName);
            if (dataset != null) {
                dataset.setObjectInstanceRID(instanceRid);
                DataInstanceRetrieveResult retrieveResult = dataset.getObjectProperties(exploreParameters);
                if (retrieveResult != null) {
                    log.info("Query result statistics: {}",
                            (new Gson()).toJson(retrieveResult.getOperationStatistics()));
                    List<DataInstance> dataInstanceList = retrieveResult.getDataInstances();
                    if (dataInstanceList != null && dataInstanceList.size() > 0) {
                        boolean flag = true;
                        for (DataInstance dataInstance : dataInstanceList) {
                            // queryResults.add(dataInstance.getDataProperties());
                            CollectionInstancesQueryOutput queryOutput = new CollectionInstancesQueryOutput();
                            if (flag) {
                                try {
                                    Map<String, Object> intsanceData = dataInstance.getDataProperties();
                                    if (intsanceData != null && intsanceData.containsKey(CimConstants.EXTERNAL_DATA_SET_TOTAL_COUNT)) {
                                        totalCount =
                                                (Long) intsanceData.get(CimConstants.EXTERNAL_DATA_SET_TOTAL_COUNT);
                                    }
                                } catch (Exception e) {
                                    log.error("get collection total count failed", e);
                                }
                                flag = false;
                            }
                            queryOutput.setInstanceData(dataInstance.getDataProperties());
                            queryOutput.setInstanceRid(dataInstance.getDataInstanceRID());
                            queryResults.add(queryOutput);
                        }
                    }
                    if (!dataSetName.equals("envHistoryWarningDataset") && !dataSetName.equals("HistoryMalDataset")) {
                        totalCount = getQueryTotalCount(retrieveResult.getOperationStatistics().getResultDataNumber()
                                , queryInput);
                        if (totalCount == -1L) {
                            exploreParameters.setStartPage(0);
                            exploreParameters.setResultNumber(0);
                            retrieveResult = dataset.getObjectProperties(exploreParameters);
                            totalCount = retrieveResult.getOperationStatistics().getResultDataNumber();
                        }
                    }
                }
            } else {
                String msg = String.format(DATA_SET_NOT_FOUND, dataSetName, objectTypeId);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040404, msg);
            }
        }

        CollectionQueryOutput queryOutput = new CollectionQueryOutput();
        queryOutput.setTotalCount(totalCount);
        queryOutput.setInstances(queryResults);

        return queryOutput;
    }

    /**
     * 新增集合数据集实例数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetName
     * @param collectionDatasetValues
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public BatchDataOperationResult addInstanceCollection(String tenantId, String objectTypeId, String instanceRid,
                                                          String dataSetName,
                                                          List<Map<String, Object>> collectionDatasetValues) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        if (StringUtils.isNotBlank(dataSetName)) {
            Dataset dataset = infoObjectDef.getCollectionDataset(dataSetName);
            dataset.setObjectInstanceRID(instanceRid);
            if (dataset != null) {
                BatchDataOperationResult operationResult = dataset.loadObjectDataset(collectionDatasetValues);
                log.info("Collection load result: {}", (new Gson()).toJson(operationResult));
                return operationResult;
            } else {
                String msg = String.format(DATA_SET_NOT_FOUND, dataSetName, objectTypeId);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040404, msg);
            }
        }
        return null;
    }

    public BatchDataOperationResult deleteInstanceCollection(String tenantId, String objectTypeId, String instanceRid
            , String dataSetName, List<String> dataSetInstanceRids) throws DataServiceModelRuntimeException,
            EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        if (StringUtils.isNotBlank(dataSetName)) {
            Dataset dataset = infoObjectDef.getCollectionDataset(dataSetName);
            dataset.setObjectInstanceRID(instanceRid);
            if (dataset != null) {
                BatchDataOperationResult operationResult = dataset.deleteObjectDataset(dataSetInstanceRids);
                log.info("Collection load result: {}", (new Gson()).toJson(operationResult));
                return operationResult;
            } else {
                String msg = String.format(DATA_SET_NOT_FOUND, dataSetName, objectTypeId);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040404, msg);
            }
        }
        return null;
    }

    public Map<String, Long> countByObjectType(String tenantId, List<String> objectTypeIds) {
        Map<String, Long> objectInstanceCount = new HashMap<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
        if (objectTypeIds != null && objectTypeIds.size() > 0) {
            for (String objectTypeId : objectTypeIds) {
                objectInstanceCount.put(objectTypeId, -1L);
                InfoObjectDef infoObjectDef;
                try {
                    infoObjectDef = infoObjectDefs.getInfoObjectDef(objectTypeId);
                    if (infoObjectDef != null) {
                        objectInstanceCount.put(objectTypeId, infoObjectDef.countAllInstance());
                    } else {
                        log.info("=== object type not found");
                    }
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return objectInstanceCount;
    }


    /**
     * 尝试根据一次分页查询结果获取该条件的查询总量
     *
     * @param retrieveCount
     * @param queryInput
     * @return -1：获取总量失败
     */
    private Long getQueryTotalCount(Long retrieveCount, InstancesQueryInput queryInput) {
        Long totalCount = -1L;

        if (queryInput.getPageIndex() >= 0 && queryInput.getPageSize() > 0) {
            if (retrieveCount < queryInput.getPageSize()) {
                totalCount = queryInput.getPageIndex() * queryInput.getPageSize() + retrieveCount;
            } else {
                log.info("can not get total count from query result");
            }
        } else {
            totalCount = retrieveCount;
        }

        return totalCount;
    }

    /**
     * 批量更新单实例
     *
     * @param objectValueList
     * @return
     */
    public List<InfoObjectValue> updateSingleObjectValuesFormal(List<Map<String, Object>> objectValueList) {//NOSONAR
        List<InfoObjectValue> formalObjectValueList = new ArrayList<>();

        if (objectValueList == null) {
            formalObjectValueList = null;
        } else {
            for (Map<String, Object> map : objectValueList) {
                InfoObjectValue objectValue = new InfoObjectValue();
                if (!map.containsKey(CimConstants.INPUT_INSTANCE_RID)) {
                    log.error("instance rid is mandary");
                    continue;
                } else {
                    objectValue.setObjectInstanceRID(map.get(CimConstants.INPUT_INSTANCE_RID).toString());
                }
                Map<String, Object> baseValues = new HashMap<>();
                Map<String, Object> generalValues = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals(CimConstants.INPUT_INSTANCE_RID)) {
                        continue;
                    }
                    if (ExternalConfigFilesReader.defaultBaseDataSetNames.contains(entry.getKey())) {
                        baseValues.put(entry.getKey(), entry.getValue());
                    } else {
                        generalValues.put(entry.getKey(), entry.getValue());
                    }
                }
                objectValue.setBaseDatasetPropertiesValue(baseValues);
                objectValue.setGeneralDatasetsPropertiesValue(generalValues);
                formalObjectValueList.add(objectValue);
            }
        }

        return formalObjectValueList;
    }


    public Integer updateSingleObjectValuesFormal(CimDataSpace cds, String objectTypeId,
                                                  List<Map<String, Object>> objectValueList,
                                                  List<InfoObjectValue> infoObjectValueList) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {//NOSONAR
        int failedItems = 0;
        Map<String, InfoObjectValue> objectValueMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(objectValueList)) {
            for (Map<String, Object> map : objectValueList) {
                InfoObjectValue objectValue = new InfoObjectValue();
                if (!map.containsKey(CimConstants.INPUT_INSTANCE_RID)) {
                    if (map.containsKey(CimConstants.ID_PROPERTY_TYPE_NAME)) {
                        objectValueMap.put(map.get(CimConstants.ID_PROPERTY_TYPE_NAME).toString(), objectValue);
                    } else {
                        failedItems++;
                        log.warn("error input: [{}]", map);
                        continue;
                    }
                } else {
                    objectValue.setObjectInstanceRID(map.get(CimConstants.INPUT_INSTANCE_RID).toString());
                }
                Map<String, Object> baseValues = new HashMap<>();
                Map<String, Object> generalValues = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals(CimConstants.INPUT_INSTANCE_RID)) {
                        continue;
                    }
                    if (ExternalConfigFilesReader.defaultBaseDataSetNames.contains(entry.getKey())) {
                        baseValues.put(entry.getKey(), entry.getValue());
                    } else {
                        if (entry.getValue() instanceof List) {
                            List list = (ArrayList) entry.getValue();
                            Object[] objectArray = listToArray(list);
                            generalValues.put(entry.getKey(), objectArray);
                        } else {
                            generalValues.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                objectValue.setBaseDatasetPropertiesValue(baseValues);
                objectValue.setGeneralDatasetsPropertiesValue(generalValues);
                infoObjectValueList.add(objectValue);
            }
        }
        log.debug("instance rid and id not found count: [{}]", failedItems);
        if (objectValueMap.size() > 0) {
            List<Object> idList = new ArrayList<>(objectValueMap.keySet());
            List<Fact> factList = queryInheritFactById(cds, objectTypeId, idList);
            log.debug("instance rid not found by id count: [{}]", objectValueMap.size() - factList.size());
            failedItems += (objectValueMap.size() - factList.size());
            if (CollectionUtils.isNotEmpty(factList)) {
                for (Fact fact : factList) {
                    if (fact.hasProperty(CimConstants.ID_PROPERTY_TYPE_NAME) && fact.getProperty(CimConstants.ID_PROPERTY_TYPE_NAME).getPropertyValue() != null) {
                        InfoObjectValue objectValue =
                                objectValueMap.get(fact.getProperty(CimConstants.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString());
                        if (objectValue != null) {
                            objectValue.setObjectInstanceRID(fact.getId());
                        }
                    }
                }
            }
        }

        return failedItems;
    }

    /**
     * 批量新增单实例
     *
     * @param objectValueList
     * @return
     */
    public List<InfoObjectValue> addSingleObjectValuesFormal(List<Map<String, Object>> objectValueList) {
        List<InfoObjectValue> formalObjectValueList = new ArrayList<>();

        if (objectValueList == null) {
            formalObjectValueList = null;
        } else {
            for (Map<String, Object> map : objectValueList) {
                Map<String, Object> baseValues = new HashMap<>();
                Map<String, Object> generalValues = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (ExternalConfigFilesReader.defaultBaseDataSetNames.contains(entry.getKey())) {
                        baseValues.put(entry.getKey(), entry.getValue());
                    } else {


                        if (entry.getValue() instanceof List) {
                            List list = (ArrayList) entry.getValue();
                            Object[] objectArray = listToArray(list);


                            generalValues.put(entry.getKey(), objectArray);

                        } else {
                            generalValues.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                InfoObjectValue objectValue = new InfoObjectValue();
                objectValue.setGeneralDatasetsPropertiesValue(generalValues);
                objectValue.setBaseDatasetPropertiesValue(baseValues);
                formalObjectValueList.add(objectValue);
            }
        }

        return formalObjectValueList;
    }

    /**
     * 由于前端传来的json之后都是集合，所以将集合转成数组
     *
     * @param list
     * @return
     */
    private Object[] listToArray(List list) {
        Object[] objectArray = new Object[]{};
        if (list != null && list.size() > 0) {
            Object o = list.get(0);
            if (o instanceof Boolean) {
                objectArray = list.stream().toArray(Boolean[]::new);
            } else if (o instanceof Integer) {
                objectArray = list.stream().toArray(Integer[]::new);
            } else if (o instanceof Short) {
                objectArray = list.stream().toArray(Short[]::new);
            } else if (o instanceof Long) {
                objectArray = list.stream().toArray(Long[]::new);
            } else if (o instanceof Float) {
                objectArray = list.stream().toArray(Float[]::new);
            } else if (o instanceof Double) {
                objectArray = list.stream().toArray(Double[]::new);
            } else if (o instanceof Date) {
                objectArray = list.stream().toArray(Date[]::new);
            } else if (o instanceof String) {
                objectArray = list.stream().toArray(String[]::new);
            } else {
                throw new UnsupportedOperationException("暂不支持此种格式");
            }
        }
        return objectArray;
    }

    private List<Fact> queryInheritFactById(CimDataSpace cds, String objectTypeId, List<Object> idList) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        FilteringItem filteringItem = new InValueFilteringItem(CimConstants.ID_PROPERTY_TYPE_NAME, idList);
        ExploreParameters ep = new ExploreParameters();
        ep.setType(objectTypeId);
        ep.setDefaultFilteringItem(filteringItem);
        InformationExplorer ie = cds.getInformationExplorer();
        return ie.discoverInheritFacts(ep);
    }

    public CompositeQueryOutput compositeQuery(String objectTypeId, InstancesQueryInput queryInput,
                                               List<String> dataSetNames, String userId, String tenantId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        ExploreParameters exploreParameters = parserQueryInput(objectTypeId, queryInput, true);
        // Map<String, ExploreParameters> map = dataSetNames.stream().collect(Collectors.toMap(name -> name, null));
        Map<String, ExploreParameters> map = new HashMap<>();
        for (String dataSetName : dataSetNames) {
            map.put(dataSetName, new ExploreParameters());
        }
        InfoObjectWithMergedDatasetPropertiesRetrieveResult infoObjectWithMergedDatasetPropertiesRetrieveResult =
                infoObjectDef.getObjects(exploreParameters, map);
        //总数
        long resultDataNumber =
                infoObjectWithMergedDatasetPropertiesRetrieveResult.getOperationStatistics().getResultDataNumber();
        CompositeQueryOutput compositeQueryOutput = new CompositeQueryOutput();
        compositeQueryOutput.setTotalCount(resultDataNumber);
        //融合查询的
        List<InfoObjectWithMergedDatasetProperties> infoObjectWithMergedDatasetProperties =
                infoObjectWithMergedDatasetPropertiesRetrieveResult.getInfoObjectWithMergedDatasetProperties();
        //返回的结果VO
        List<CompositeQueryResult> compositeQueryResults = new ArrayList<>();
        for (InfoObjectWithMergedDatasetProperties infoObjectWithMergedDatasetProperty :
                infoObjectWithMergedDatasetProperties) {
            CompositeQueryResult compositeQueryResult = new CompositeQueryResult();
            InfoObject infoObject = infoObjectWithMergedDatasetProperty.getInfoObject();
            compositeQueryResult.setInstanceRid(infoObject.getObjectInstanceRID());
            List<Map<String, Object>> dataInstancesProperties =
                    infoObjectWithMergedDatasetProperty.getDataInstancesProperties();
            if (!dataInstancesProperties.isEmpty()) {
                compositeQueryResult.setDataInstancesProperties(dataInstancesProperties);
            }
            compositeQueryResults.add(compositeQueryResult);
            compositeQueryOutput.setDataInstances(compositeQueryResults);
        }
        return compositeQueryOutput;
    }

    public CollectionQueryOutput queryObjectCollection(String tenantId, String objectTypeId, String instanceRid,
                                                       String dataSetName, InstancesQueryInput queryInput) throws DataServiceModelRuntimeException, EntityNotFoundException {//NOSONAR
        List<CollectionInstancesQueryOutput> queryResults = new ArrayList<>();
        Long count = 0L;
        String elementId = "";
        ExploreParameters exploreParameters = parserStringQueryInput(queryInput);
        if (exploreParameters != null) {
            //element_id
            //elementId = exploreParameters.getDefaultFilteringItem().getAttributeName();
            elementId = queryInput.getConditions().get(0).getFirstParam();
        }
        List<BimfaceVO> resultList = new ArrayList<>();

//		String[] arr = StringUtils.split(objectTypeId, "_",2);
//		if (arr != null) {
//			String fileId = arr[1];

        File bimfaceFile = new File(offlinePkgPath + objectTypeId + ".db");

        if (bimfaceFile.exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + bimfaceFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("sqlite connector is error, msg is {}", e.getMessage());
            }
            DataContext dataContext = new JdbcDataContext(connection);
            //table element and thisColumn
            Table element = dataContext.getDefaultSchema().getTableByName("element");
            Column id = element.getColumnByName("id");
            Column family_type_id = element.getColumnByName("family_type_id");

            //table element_property and thisColumn
            Table element_property = dataContext.getDefaultSchema().getTableByName("element_property");
            Column element_id = element_property.getColumnByName("element_id");

            //table family_type_property and thisColumn
            Table family_type_property = dataContext.getDefaultSchema().getTableByName("family_type_property");
            Column type_id = family_type_property.getColumnByName("type_id");
            Column name = family_type_property.getColumnByName("name");
            Column value = family_type_property.getColumnByName("value");
            Column unit = family_type_property.getColumnByName("unit");
            Column value_type = family_type_property.getColumnByName("value_type");
            Column group_name = family_type_property.getColumnByName("group_name");
            //sql1
            Query elementQuery = new Query();
            elementQuery.from(element).select("specialty", "specialty").select("level_name", "floor")
                    .select("category_id", "categoryId").select("category_name", "categoryName")
                    .select("family_name", "family").select("family_type_name", "familyType")
                    .select("family_type_id", "familyTypeId").where(id, OperatorType.EQUALS_TO, elementId);
            DataSet eDs = dataContext.executeQuery(elementQuery);
            List<Map<String, Object>> listMap = new ArrayList<>();
            List<Map<String, Object>> listMapEnd = new ArrayList<>();
            toList(eDs, listMap);

            listMap.forEach(e -> e.forEach((k, v) -> {
                HashMap newMap = new HashMap();
                newMap.put("key", k);
                newMap.put("value", v);
                listMapEnd.add(newMap);
            }));
            BimfaceVO bimfaceVO = new BimfaceVO();
            bimfaceVO.setGroup("基础属性");
            bimfaceVO.setItems(listMapEnd);
            resultList.add(bimfaceVO);
            // sql2
            Query query = new Query();
            query.from(element_property).where(element_id, OperatorType.EQUALS_TO, elementId).select("name").select("value").select("unit").select("value_type").select("group_name");
            DataSet epDs = dataContext.executeQuery(query);
            listMap.clear();
            toList(epDs, listMap);
            //sql2
            Query fQuery = new Query();
            fQuery.from(element, family_type_property, JoinType.LEFT, family_type_id, type_id).where(id, OperatorType.EQUALS_TO, elementId).select(name).select(value).select(unit).select(value_type).select(group_name);
            DataSet fDs = dataContext.executeQuery(fQuery);
            toList(fDs, listMap);
            //用stream分组 get remove 可以过滤元素
            Map<String, List<Map<String, Object>>> elementByGroupName = listMap.stream().
                    collect(Collectors.groupingBy(e -> e.remove("groupName").toString()));
            for (Map.Entry<String, List<Map<String, Object>>> entry : elementByGroupName.entrySet()) {
                bimfaceVO = new BimfaceVO();
                bimfaceVO.setGroup(entry.getKey());
                bimfaceVO.setItems(entry.getValue());
                resultList.add(bimfaceVO);
            }
        }
//		}

        List<CollectionInstancesQueryOutput> collect = resultList.stream().map(this::covert).collect(Collectors.toList());

        CollectionQueryOutput queryOutput = new CollectionQueryOutput();
        queryOutput.setTotalCount(Long.valueOf(collect.size()));
        queryOutput.setInstances(collect);
        return queryOutput;
    }

    private CollectionInstancesQueryOutput covert(BimfaceVO bimfaceVO) {
        String group = bimfaceVO.getGroup();
        List<Map<String, Object>> items = bimfaceVO.getItems();
        CollectionInstancesQueryOutput output = new CollectionInstancesQueryOutput();
        Map<String, Object> outMap = new HashMap<>();
        outMap.put("group", group);
        outMap.put("items", items);
        output.setInstanceData(outMap);
        return output;
    }

    private void toList(DataSet ds, List<Map<String, Object>> listMap) {
        List<SelectItem> selectItemsList = ds.getSelectItems();
        Iterator<Row> dataRowIterator = ds.iterator();
        while (dataRowIterator.hasNext()) {
            Map<String, Object> dataValueMap = new HashMap<>();
            Row currentRow = dataRowIterator.next();
            if (selectItemsList != null) {
                for (SelectItem selectItem : selectItemsList) {
                    Object value = currentRow.getValue(selectItem);
                    if (value != null) {
                        if (selectItem.getAlias() != null) {
                            dataValueMap.put(selectItem.getAlias(), value);
                        } else if (selectItem.getExpression() != null) {
                            String camel = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, selectItem.getExpression());
                            dataValueMap.put(camel, value);
                        } else {
                            String columnName = selectItem.getColumn().getName();
                            if ("name".equals(columnName)) {
                                dataValueMap.put("key", value);
                            } else {
                                String camel = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
                                dataValueMap.put(camel, value);
                            }
                        }
                    }
                }
            }
            listMap.add(dataValueMap);
        }
        log.info("list.size is {}", listMap.size());
    }

    /**
     * 根据条件查询
     *
     * @param tenantId     租户
     * @param objectTypeId 对象类型
     * @param cimIdList    id集合
     * @return 返回结果提供实例名称
     */
    public OutputQueryFroNameVO queryForMame(String tenantId, String objectTypeId, List<String> cimIdList) {
        OutputQueryFroNameVO outputQueryFroNameVO = new OutputQueryFroNameVO();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        cimModelCore.setCimDataSpace(cds);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        if (infoObjectDef == null) {
            log.error("object type of [{}] not exists or not belong to tenant of [{}]", objectTypeId, tenantId);
            return outputQueryFroNameVO;
        }
        if (cimIdList != null && cimIdList.size() != 0) {
            List<InfoObject> infoObjectList = new ArrayList<>();
            for (String cimId : cimIdList) {
                InfoObject infoObj = infoObjectDef.getObjectByID(cimId);
                if (infoObj != null) {
                    infoObjectList.add(infoObj);
                }

            }
            AtomicInteger count = new AtomicInteger();
            ArrayList<OutputQueryFroNameBean> outputQueryFroNameBeanList = new ArrayList<>();

            infoObjectList.forEach(i -> {
                if (i != null) {
                    try {
                        OutputQueryFroNameBean outputQueryFroNameBean = new OutputQueryFroNameBean();
                        String idStr = (String) i.getPropertyValue(CimConstants.GeneralProperties.ID);
                        String nameStr = (String) i.getPropertyValue(CimConstants.GeneralProperties.NAME);
                        String objectTypeName = i.getObjectTypeName();
                        outputQueryFroNameBean.setNAME(nameStr);
                        outputQueryFroNameBean.setID(idStr);
                        outputQueryFroNameBean.setObjectType(objectTypeName);
                        outputQueryFroNameBeanList.add(outputQueryFroNameBean);
                        count.getAndIncrement();

                    } catch (DataServiceModelRuntimeException e) {
                        log.error("error is [{}]", e.getMessage());
                    }
                }
            });
            outputQueryFroNameVO.setInstanceInfoList(outputQueryFroNameBeanList);
            outputQueryFroNameVO.setTotalCount(count);
        }

        return outputQueryFroNameVO;
    }

    public SingleInstancesQueryOutput queryByRID(String tenantId, String objectTypeId, String rid) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);

            InfoObject infoObject = infoObjectDef.getObject(rid);

            List<DatasetVO> linkedDatasets = InfoObjectFeatures.getLinkedDatasets(cds, objectTypeId, true);

            SingleInstancesQueryOutput queryOutput = new SingleInstancesQueryOutput();
            queryOutput.setInstanceRid(infoObject.getObjectInstanceRID());
            ((InfoObjectDSImpl) infoObject).setLinkedDatasets(linkedDatasets);

            Map<String, Map<String, Object>> tmpMap = infoObject.getObjectPropertiesByDatasets();
            if (tmpMap == null) {
                tmpMap = new HashMap<>();
            }
            tmpMap.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
            queryOutput.setInstanceData(tmpMap);

            return queryOutput;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cds.closeSpace();
        }
        return null;
    }

    public BatchDataOperationResult deleteInstanceSingleByCondition(String tenantId, String objectTypeId,
                                                                    DeleteByConditionInputBean inputBean) throws InputErrorException {
        BatchDataOperationResult dataOperationResult = new BatchDataOperationResultDSImpl();
        CimDataSpace cds = null;
        try {
            String property = inputBean.getProperty();
            String relatedObject = inputBean.getRelatedObjectTypeId();
            String relatedProperty = inputBean.getRelatedProperty();

            List<CommonQueryConditionsBean> conditions = inputBean.getConditions();
            if (conditions.isEmpty()) {
                log.info("删除条件不可为空");
                throw new InputErrorException("condition is mandatory");
            }

            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            ExploreParameters exploreParameters = QueryConditionParser.parserQueryInput(cds, objectTypeId, inputBean,
                    false);
            exploreParameters.setStartPage(0);
            exploreParameters.setResultNumber(Integer.MAX_VALUE);
            if (exploreParameters == null || exploreParameters.getDefaultFilteringItem() == null) {
                log.info("解析删除条件失败，删除条件不可为空");
                throw new InputErrorException("input condition is wrong");
            }

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(exploreParameters);
            if (retrieveResult == null || CollectionUtils.isEmpty(retrieveResult.getInfoObjects())) {
                ((BatchDataOperationResultDSImpl) dataOperationResult).finishBatchDataOperation();
                dataOperationResult.getOperationStatistics().setOperationSummary("no instance is deleted");
                return dataOperationResult;
            }

            List<InfoObject> objectList = retrieveResult.getInfoObjects();
            List<String> rids = new ArrayList<>();
            objectList.forEach(p -> rids.add(p.getObjectInstanceRID()));
            log.debug("should be delete instance rids: [{}]", JSON.toJSON(rids));
            if (StringUtils.isNotBlank(relatedObject) || StringUtils.isNotBlank(relatedProperty) || StringUtils.isNotBlank(property)) {
                //删除关联数据
                log.debug("start to delete related instances");
                List<Object> propertyValues = new ArrayList<>();
                objectList.forEach(p -> getPropertyByName(propertyValues, p, property));
                if (CollectionUtils.isNotEmpty(propertyValues)) {
                    ExploreParameters ep = new ExploreParameters();
                    ep.setType(relatedObject);
                    ep.setResultNumber(Integer.MAX_VALUE);
                    FilteringItem filteringItem = new InValueFilteringItem(relatedProperty, propertyValues);
                    ep.setDefaultFilteringItem(filteringItem);

                    InfoObjectDef relatedObjectDef = modelCore.getInfoObjectDef(relatedObject);
                    if (relatedObjectDef != null) {
                        InfoObjectRetrieveResult relatedRetrieveResult = relatedObjectDef.getObjects(ep);
                        if (relatedRetrieveResult != null && CollectionUtils.isNotEmpty(relatedRetrieveResult.getInfoObjects())) {
                            List<InfoObject> relatedInfoObjectList = relatedRetrieveResult.getInfoObjects();
                            List<String> relatedRids = new ArrayList<>();
                            relatedInfoObjectList.forEach(p -> relatedRids.add(p.getObjectInstanceRID()));
                            BatchDataOperationResult relatedOperationResult = relatedObjectDef.deleteObjects(relatedRids);
                            log.info("delete related instance result: [{}]", JSON.toJSON(relatedOperationResult));
                        } else {
                            log.info("no related instance is found");
                        }
                    } else {
                        log.info("related info object type not found: [{}]", relatedObject);
                    }
                } else {
                    log.info("related property value is empty");
                }
            } else {
                log.debug("no related instance should be deleted");
            }
            dataOperationResult = infoObjectDef.deleteObjects(rids);
            return dataOperationResult;
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    private void getPropertyByName(List<Object> objectList, InfoObject infoObject, String property) {
        try {
            objectList.add(infoObject.getPropertyValue(property));
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        }
    }

}
