package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DataInstance;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.Dataset;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.DataInstanceRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimsvc.model.QueryInputBean;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import com.google.gson.Gson;
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
public class ObjectInstanceService {
    private static Logger log = LoggerFactory.getLogger(ObjectInstanceService.class);

    @Autowired
    private ObjectMapper objectMapper;

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
    public List<DatasetVO> getDataSetDef(String tenantId, String objectTypeId, String dataSetName) throws DataServiceModelRuntimeException, EntityNotFoundException {
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
                    voList.add(DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, datasetDef.getDatasetRID(), true));
                }
            }
        } else {
            List<DatasetDef> dataSetDefList = infoObjectDef.getDatasetDefs();
            if (dataSetDefList != null) {
                for (DatasetDef def : dataSetDefList) {
                    // voList.add(def.getDataSetVO(true));
                    voList.add(DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, def.getDatasetRID(), true));
                }
            }
            // voList.add(infoObjectDef.getBaseDatasetDef().getDataSetVO(true));
            voList.add(DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName));
        }

        return voList;
    }

    /**
     * 查询实例数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetName  返回属于该属性集的数据，若为空则返回所有single数据
     * @param conditions   分页属性非空，查询条件为空则返回所有数据
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public List<Map<String, Map<String, Object>>> queryInstanceSingle(String tenantId, String objectTypeId, String dataSetName, QueryInputBean conditions) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        ExploreParameters exploreParameters = parserQueryInput(objectTypeId, conditions, false);
        try {
            log.info("query condition input: {}", objectMapper.writeValueAsString(exploreParameters));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);

        List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
        log.info("Query instance count {}", infoObjectList.size());
        List<Map<String, Map<String, Object>>> result = new ArrayList<>();
//        List<Map<String, Map<String, Object>>> result = new ArrayList<>();
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject infoObject : infoObjectList) {
                if (StringUtils.isBlank(dataSetName)) {
                    Map tmpMap = infoObject.getObjectPropertiesByDatasets();
                    tmpMap.put(CimConstants.INSTANCE_RID, infoObject.getObjectInstanceRID());
                    tmpMap.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
                    result.add(tmpMap);
                } else {
                    Map tmpMap = new HashMap<>();
                    tmpMap.put(dataSetName, infoObject.getObjectPropertiesByDataset(dataSetName));
                    tmpMap.put(CimConstants.INSTANCE_RID, infoObject.getObjectInstanceRID());
                    result.add(tmpMap);
                }
            }
        }

        return result;
    }

    /**
     * 新增单条实例数据
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectValueList
     * @param isAddRelation
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public BatchDataOperationResult addInstanceSingle(String tenantId, String objectTypeId, List<InfoObjectValue> objectValueList, boolean isAddRelation) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        BatchDataOperationResult dataOperationResult = infoObjectDef.newObjects(objectValueList, isAddRelation);
        return dataOperationResult;
    }

    /**
     * 解析查询条件
     *
     * @param objectTypeId
     * @param queryInput
     * @return
     */
    private static ExploreParameters parserQueryInput(String objectTypeId, QueryInputBean queryInput, boolean isIncludeCollectionDataSet) {
        ExploreParameters exploreParameters = null;
        if (queryInput != null && queryInput.getPageSize() != 0) {
            exploreParameters = new ExploreParameters();
            exploreParameters.setStartPage(queryInput.getStartPage());
            exploreParameters.setEndPage(queryInput.getEndPage());
            exploreParameters.setPageSize(queryInput.getPageSize());

            List<QueryConditionsBean> queryConditionsBeanList = queryInput.getConditions();
            if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
                for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                    FilteringItem filteringItem = QueryConditionsUtil.parseQueryCondition(objectTypeId, queryConditionsBeanList.get(i), isIncludeCollectionDataSet);
                    if (i == 0) {
                        exploreParameters.setDefaultFilteringItem(filteringItem);
                    } else {
                        exploreParameters.addFilteringItem(filteringItem, queryConditionsBeanList.get(i).getFilterLogical());
                    }
                }
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
     * @param conditions
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public List<Map<String, Object>> queryInstanceCollection(String tenantId, String objectTypeId, String instanceRid, String dataSetName, QueryInputBean conditions) throws DataServiceModelRuntimeException, EntityNotFoundException {
        List<Map<String, Object>> queryResult = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        ExploreParameters exploreParameters = parserQueryInput(objectTypeId, conditions, true);

        if (StringUtils.isBlank(dataSetName)) {
            log.error("data set name is blank");
            return queryResult;
        }

        Dataset dataset = infoObjectDef.getCollectionDataset(dataSetName);
        if (dataset == null) {
            String msg = String.format("DataSet=%s of ObjectType=%s not found!", dataSetName, objectTypeId);
            throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040404, msg);
        }

        dataset.setObjectInstanceRID(instanceRid);
        DataInstanceRetrieveResult retrieveResult = dataset.getObjectProperties(exploreParameters);
        if (retrieveResult != null) {
            log.info("Query result statistics: {}", (new Gson()).toJson(retrieveResult.getOperationStatistics()));
            List<DataInstance> dataInstanceList = retrieveResult.getDataInstances();
            if (dataInstanceList != null && dataInstanceList.size() > 0) {
                for (DataInstance dataInstance : dataInstanceList) {
                    queryResult.add(dataInstance.getDataProperties());
                }
            }
        }

        return queryResult;
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
    public BatchDataOperationResult addInstanceCollection(String tenantId, String objectTypeId, String instanceRid, String dataSetName, List<Map<String, Object>> collectionDatasetValues) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        if (StringUtils.isNotBlank(dataSetName)) {
            Dataset dataset = infoObjectDef.getCollectionDataset(dataSetName);
            dataset.setObjectInstanceRID(instanceRid);
            if (dataset != null) {
                BatchDataOperationResult operationResult = dataset.loadObjectDataset(collectionDatasetValues);
                log.info("Collection load result: {}", (new Gson()).toJson(operationResult));
                return operationResult;
            } else {
                String msg = String.format("DataSet=%s of ObjectType=%s not found!", dataSetName, objectTypeId);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040404, msg);
            }
        }
        return null;
    }


    public Map<String, Long> countByObjectType(String tenantId, List<String> objectTypeIds) {
        Map<String, Long> objectInstanceCount = new HashMap<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        // CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
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
                            log.info("===object type of {} not found", objectTypeId);
                        }
                    } catch (CimDataEngineInfoExploreException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineRuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return objectInstanceCount;
    }

}
