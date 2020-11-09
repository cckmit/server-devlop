package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.Dataset;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.DatasetBean;
import com.glodon.pcop.cimsvc.model.gcAdapters.DataSetAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanjk
 * @description Base属性集
 * @date 2018/9/20 16:52
 */
@Service
public class BaseDatasetService {
    private static Logger log = LoggerFactory.getLogger(BaseDatasetService.class);
    private static final String INSTANCE_NOT_FOUND = "Instance is not found: instanceId=%s";

    /**
     * 更新base属性集数据
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @param values
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public boolean update(String tenantId, String infoObjectTypeName, String instanceId, Map<String, Object> values) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            Map<String, Object> formalValues = OrientDBCommonUtil.valuesTypeCast(OrientDBCommonUtil.baseDataSetPropertyType(), values);
            return infoObject.update(formalValues);
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 查询指定实例的base属性值
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public Map<String, Object> getInfo(String tenantId, String infoObjectTypeName, String instanceId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            return infoObject.getInfo();
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 获取GeneralDatasets的所有属性定义
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @return
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public Map<String, List<PropertyTypeDef>> getPropertyDefsByDatasets(String tenantId, String infoObjectTypeName, String instanceId) throws EntityNotFoundException, DataServiceModelRuntimeException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            return infoObject.getPropertyDefsByDatasets();
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }

    /**
     * 获取collection属性集定义
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public List<DatasetBean> getCollectionDatasets(String tenantId, String infoObjectTypeName, String instanceId) throws EntityNotFoundException, DataServiceModelRuntimeException {
        List<DatasetBean> datasetBeanList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            List<Dataset> datasetList = infoObject.getCollectionDatasets();
            if (datasetList != null) {
                for (Dataset dataset : datasetList) {
                    String dataSetId = dataset.getDatasetRID();
                    DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, dataSetId, true);
                    datasetBeanList.add(DataSetAdapter.typeCast(datasetVO));
                }
            }
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return datasetBeanList;
    }

    /**
     * single属性集数据查询
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @return
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public Map<String, Map<String, Object>> getObjectPropertiesByDatasets(String tenantId, String infoObjectTypeName, String instanceId) throws EntityNotFoundException, DataServiceModelRuntimeException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            return infoObject.getObjectPropertiesByDatasets();
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }


    /**
     * single属性集数据更新
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @return
     */
    public boolean updateObjectPropertiesByDatasets(String tenantId, String infoObjectTypeName, String instanceId, Map<String, Map<String, Object>> propertiesValueOfEachChangedGeneralDataset) throws EntityNotFoundException, DataServiceModelRuntimeException {
        List<DatasetBean> datasetBeanList = new ArrayList<>();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject == null) {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        List<Dataset> datasetList = infoObject.getCollectionDatasets();
        if (datasetList == null) {
            log.error("not data set found");
            return infoObject.updateObjectPropertiesByDatasets(propertiesValueOfEachChangedGeneralDataset);
        }
        //属性数据类型转换
        for (Dataset dataset : datasetList) {
            if (propertiesValueOfEachChangedGeneralDataset.containsKey(dataset.getDatasetName())) {
                List<PropertyTypeDef> propertyTypeDefList = dataset.getDatasetDef().getPropertyTypeDefs();
                if (propertyTypeDefList != null) {
                    Map<String, String> dataTypeMap = new HashMap<>();
                    for (PropertyTypeDef propertyTypeDef : propertyTypeDefList) {
                        dataTypeMap.put(propertyTypeDef.getPropertyTypeName(), propertyTypeDef.getPropertyFieldDataClassify());
                    }
                    Map<String, Object> formalValueMap = OrientDBCommonUtil.valuesTypeCast(dataTypeMap, propertiesValueOfEachChangedGeneralDataset.get(dataset.getDatasetName()));
                    propertiesValueOfEachChangedGeneralDataset.put(dataset.getDatasetName(), formalValueMap);
                }
            }
        }
        return infoObject.updateObjectPropertiesByDatasets(propertiesValueOfEachChangedGeneralDataset);
    }

    /**
     * single属性集数据删除
     *
     * @param tenantId
     * @param infoObjectTypeName
     * @param instanceId
     * @param generalDatasetsName
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public boolean deleteObjectPropertiesByDatasets(String tenantId, String infoObjectTypeName, String instanceId, List<String> generalDatasetsName) throws DataServiceModelRuntimeException, EntityNotFoundException {
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, infoObjectTypeName);
        InfoObject infoObject = infoObjectDef.getObject(instanceId);
        if (infoObject != null) {
            return infoObject.deleteObjectPropertiesByDatasets(generalDatasetsName);
        } else {
            String msg = String.format(INSTANCE_NOT_FOUND, instanceId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }
}
