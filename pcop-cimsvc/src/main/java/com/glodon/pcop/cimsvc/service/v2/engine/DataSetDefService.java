package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cim.engine.dataServiceCache.model.PropertyTypeDefCache;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeRestrictFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.ApiRunTimeException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DataSetDefService {
    private static Logger log = LoggerFactory.getLogger(DataSetDefService.class);

    //业务id
    private static final String BUSINESS_ID_KEY = "ID";

    /**
     * 获取指定实例的指定属性集的数据
     *
     * @param tenantId
     * @param infoObjectName
     * @param datSetName
     * @param businessId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public List<Map<String, Object>> getDataSetValueById(String tenantId, String infoObjectName, String datSetName,
                                                         String businessId) throws DataServiceModelRuntimeException {

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        List<Map<String, Object>> results = new ArrayList<>();
        if (cimModelCore != null) {
            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectName);
            if (infoObjectDef != null) {
                ExploreParameters exploreParameters = new ExploreParameters();
                FilteringItem filteringItem = new EqualFilteringItem(BUSINESS_ID_KEY, businessId);
                exploreParameters.setDefaultFilteringItem(filteringItem);
                InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
                List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
                if (infoObjectList != null) {
                    for (InfoObject infoObject : infoObjectList) {
                        Map<String, Object> dataSetValue = infoObject.getObjectPropertiesByDataset(datSetName);
                        if (dataSetValue != null) {
                            results.add(dataSetValue);
                        }
                    }
                }

            }
        }
        return results;
    }



    /**
     * 新增属性集定义，包含属性定义，属性的限制条件定义(无对象)
     * @param userId
     * @param tenantId
     * @param datasetVO
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DatasetDef addDataSetAndPropertyDefWithoutObjectType(String userId, String tenantId, DatasetVO datasetVO) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        DatasetDef datasetDef = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            String primaryKey = getDataSetPrimaryKey(datasetVO);
            log.info("Primary key of {} is {}", datasetVO.getDatasetName(), primaryKey);
            datasetVO.setPrimaryKey(primaryKey);
            //新增属性集定义
            DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
//        DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
            datasetDef = datasetDefs.addDatasetDef(datasetVO);
            log.info("Add data set: {}", datasetVO.getDatasetName());
            if (datasetVO.getLinkedPropertyTypes() != null) {
                //新增属性集关联的属性
                PropertyTypeDefs propertyTypeDefs = cimModelCore.getPropertyTypeDefs();
                for (PropertyTypeVO propertyTypeVO : datasetVO.getLinkedPropertyTypes()) {
                    PropertyTypeDef tmpPropertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
                    log.info("Add property type: {}", propertyTypeVO.getPropertyTypeName());
                    if (tmpPropertyTypeDef != null) {
                        //属性与属性集关联
                        datasetDef.addPropertyTypeDef(tmpPropertyTypeDef);
                        //新增属性关联的限制条件
                        if (propertyTypeVO.getRestrictVO() != null) {
                            PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                            restrictVO.setDatasetId(datasetDef.getDatasetRID());
                            restrictVO.setPropertyTypeId(tmpPropertyTypeDef.getPropertyTypeRID());
                            // datasetDef.addPropertyTypeRestrict(restrictVO);
                            PropertyTypeRestrictFeatures.addRestrictToDataSet(CimConstants.defauleSpaceName,
                                    datasetDef.getDatasetRID(), restrictVO);
                            log.info("Add property restrict: {}-->{}", restrictVO.getDatasetId(),
                                    restrictVO.getPropertyTypeId());
                        }
                    }
                }
            }
            boolean addDataSetLink = false;
            log.info("addDataSetLink={}, dataSetId={}", addDataSetLink,
                    datasetDef.getDatasetRID());
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return datasetDef;
    }


    /**
     * 新增属性集定义，包含属性定义，属性的限制条件定义
     *
     * @param tenantId
     * @param objectTypeId
     * @param datasetVO
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DatasetDef addDataSetAndPropertyDef(String tenantId, String objectTypeId, DatasetVO datasetVO)
            throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        DatasetDef datasetDef = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            String primaryKey = getDataSetPrimaryKey(datasetVO);
            log.info("Primary key of {} is {}", datasetVO.getDatasetName(), primaryKey);
            datasetVO.setPrimaryKey(primaryKey);
            //新增属性集定义
            DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
//        DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
            datasetDef = datasetDefs.addDatasetDef(datasetVO);
            log.info("Add data set: {}", datasetVO.getDatasetName());
            if (datasetVO.getLinkedPropertyTypes() != null) {
                //新增属性集关联的属性
                PropertyTypeDefs propertyTypeDefs = cimModelCore.getPropertyTypeDefs();
                for (PropertyTypeVO propertyTypeVO : datasetVO.getLinkedPropertyTypes()) {
                    PropertyTypeDef tmpPropertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
                    log.info("Add property type: {}", propertyTypeVO.getPropertyTypeName());
                    if (tmpPropertyTypeDef != null) {
                        //属性与属性集关联
                        datasetDef.addPropertyTypeDef(tmpPropertyTypeDef);
                        //新增属性关联的限制条件
                        if (propertyTypeVO.getRestrictVO() != null) {
                            PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                            restrictVO.setDatasetId(datasetDef.getDatasetRID());
                            restrictVO.setPropertyTypeId(tmpPropertyTypeDef.getPropertyTypeRID());
                            // datasetDef.addPropertyTypeRestrict(restrictVO);
                            PropertyTypeRestrictFeatures.addRestrictToDataSet(CimConstants.defauleSpaceName,
                                    datasetDef.getDatasetRID(), restrictVO);
                            log.info("Add property restrict: {}-->{}", restrictVO.getDatasetId(),
                                    restrictVO.getPropertyTypeId());
                        }
                    }
                }
            }
            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
            boolean addDataSetLink;
            if (datasetVO.getDataSetType().equals(BusinessLogicConstant.DatasetType.INSTANCE)) {
                addDataSetLink = infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
            } else if (datasetVO.getDataSetType().equals(BusinessLogicConstant.DatasetType.OBJECT)) {
                addDataSetLink = infoObjectDef.addDefinitionDatasetDef(datasetDef.getDatasetRID());
            } else {
                addDataSetLink = false;
                log.error("unrecognized data set type: {}", datasetVO.getDataSetType());
            }
            log.info("addDataSetLink={}, objectTypeId={}, dataSetId={}", addDataSetLink, objectTypeId,
                    datasetDef.getDatasetRID());
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return datasetDef;
    }

    public DatasetDef addDataSetAndPropertyDef(String tenantId, String objectTypeId, CimDataSpace cds,
                                               DatasetVO datasetVO) throws DataServiceModelRuntimeException,
            CimDataEngineRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        cimModelCore.setCimDataSpace(cds);
        String primaryKey = getDataSetPrimaryKey(datasetVO);
        log.info("Primary key of {} is {}", datasetVO.getDatasetName(), primaryKey);
        datasetVO.setPrimaryKey(primaryKey);
        //新增属性集定义
        DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
        DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
        log.info("Add data set: {}", datasetVO.getDatasetName());
        if (datasetVO.getLinkedPropertyTypes() != null) {
            //新增属性集关联的属性
            PropertyTypeDefs propertyTypeDefs = cimModelCore.getPropertyTypeDefs();
            for (PropertyTypeVO propertyTypeVO : datasetVO.getLinkedPropertyTypes()) {
                PropertyTypeDef tmpPropertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
                log.info("Add property type: {}", propertyTypeVO.getPropertyTypeName());
                if (tmpPropertyTypeDef != null) {
                    //属性与属性集关联
                    datasetDef.addPropertyTypeDef(tmpPropertyTypeDef);
                    //新增属性关联的限制条件
                    if (propertyTypeVO.getRestrictVO() != null) {
                        PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                        restrictVO.setDatasetId(datasetDef.getDatasetRID());
                        restrictVO.setPropertyTypeId(tmpPropertyTypeDef.getPropertyTypeRID());
                        // datasetDef.addPropertyTypeRestrict(restrictVO);
                        PropertyTypeRestrictFeatures.addRestrictToDataSet(CimConstants.defauleSpaceName,
                                datasetDef.getDatasetRID(), restrictVO);
                        log.info("Add property restrict: {}-->{}", restrictVO.getDatasetId(),
                                restrictVO.getPropertyTypeId());
                    }
                }
            }
        }
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        boolean addDataSetLink;
        if (datasetVO.getDataSetType().equals(BusinessLogicConstant.DatasetType.INSTANCE)) {
            addDataSetLink = infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
        } else if (datasetVO.getDataSetType().equals(BusinessLogicConstant.DatasetType.OBJECT)) {
            addDataSetLink = infoObjectDef.addDefinitionDatasetDef(datasetDef.getDatasetRID());
        } else {
            addDataSetLink = false;
            log.error("unrecognized data set type: {}", datasetVO.getDataSetType());
        }
        log.info("addDataSetLink={}, objectTypeId={}, dataSetId={}", addDataSetLink, objectTypeId,
                datasetDef.getDatasetRID());
        return datasetDef;
    }

    /**
     * 更新属性集，及其关联的属性
     *
     * @param tenantId
     * @param dataSetId
     * @param datasetVO
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DatasetDef updateDataSetAndPropertyDefWithoutObjectType(String userId,String tenantId, String dataSetId,
                                                  DatasetVO datasetVO) throws DataServiceModelRuntimeException,
            CimDataEngineRuntimeException {
        CimDataSpace cds = null;
        DatasetDef datasetDef = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            cimModelCore.setCimDataSpace(cds);
            //更新属性集定义
            //更新主键
            DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
//            DatasetDef datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
            datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
            String primaryKey = updateDataSetPrimaryKey(datasetDef, datasetVO);
            datasetVO.setPrimaryKey(primaryKey);
            datasetDef = datasetDefs.updateDatasetDef(dataSetId, datasetVO);
            //clear data set and property cache
            PropertyTypeDefCache defCache = CimCacheManager.getPropertyTypeDefCache(CimConstants.defauleSpaceName);
            defCache.removeList(datasetDef.getDatasetRID());
            CacheUtil.removeDatasetVo(CimConstants.defauleSpaceName, datasetDef.getDatasetRID());
            DatasetFeatures.updateLinkedProperty(CimConstants.defauleSpaceName, dataSetId, datasetVO);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return datasetDef;
    }



    /**
     * 更新属性集，及其关联的属性
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetId
     * @param datasetVO
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DatasetDef updateDataSetAndPropertyDef(String tenantId, String objectTypeId, String dataSetId,
                                                  DatasetVO datasetVO) throws DataServiceModelRuntimeException,
            CimDataEngineRuntimeException {
        CimDataSpace cds = null;
        DatasetDef datasetDef = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                    tenantId);
            cimModelCore.setCimDataSpace(cds);
            //更新属性集定义
            //更新主键
            DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
//            DatasetDef datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
            datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
            String primaryKey = updateDataSetPrimaryKey(datasetDef, datasetVO);
            datasetVO.setPrimaryKey(primaryKey);
            datasetDef = datasetDefs.updateDatasetDef(dataSetId, datasetVO);
            //clear data set and property cache
            PropertyTypeDefCache defCache = CimCacheManager.getPropertyTypeDefCache(CimConstants.defauleSpaceName);
            defCache.removeList(datasetDef.getDatasetRID());
            CacheUtil.removeDatasetVo(CimConstants.defauleSpaceName, datasetDef.getDatasetRID());
            DatasetFeatures.updateLinkedProperty(CimConstants.defauleSpaceName, dataSetId, datasetVO);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return datasetDef;
    }

    /**
     * 删除属性集定义，属性集与对象模型的关系，不删除关联的属性
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean deleteDataSetAndPropertyDef(String tenantId, String objectTypeId, String dataSetId)
            throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        boolean result;
        DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
        result = datasetDefs.removeDatasetDef(dataSetId);
        if (result) {
            PropertyTypeDefCache defCache = CimCacheManager.getPropertyTypeDefCache(CimConstants.defauleSpaceName);
            defCache.removeList(dataSetId);
            CacheUtil.removeDatasetVo(CimConstants.defauleSpaceName, dataSetId);
        }
        return result;
    }


    /**
     * 查询属性集及其关联的属性的定义 不包含对象
     *
     * @param userId
     * @param tenantId
     * @param dataSetId
     * @return
     */
    public DatasetVO getDataSetAndPropertyDefWithoutObjectType(String userId, String tenantId, String dataSetId) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        DatasetDef datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName,
                datasetDef.getDatasetRID(), true);
//        try {
//            datasetVO = DatasetFeatures.setInherientInfo(CimConstants.defauleSpaceName, datasetVO, objectTypeId);
//        } catch (CimDataEngineRuntimeException e) {
//            throw new ApiRunTimeException(EnumWrapper.CodeAndMsg.E05010501, e);
//        } catch (CimDataEngineInfoExploreException e) {
//            throw new ApiRunTimeException(EnumWrapper.CodeAndMsg.E05010502, e);
//        }
        return datasetVO;
    }

    /**
     * 查询属性集及其关联的属性的定义
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetId
     * @return
     */
    public DatasetVO getDataSetAndPropertyDef(String tenantId, String objectTypeId, String dataSetId) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        DatasetDef datasetDef = cimModelCore.getDatasetDefByRID(dataSetId);
        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName,
                datasetDef.getDatasetRID(), true);
        try {
            datasetVO = DatasetFeatures.setInherientInfo(CimConstants.defauleSpaceName, datasetVO, objectTypeId);
        } catch (CimDataEngineRuntimeException e) {
            throw new ApiRunTimeException(EnumWrapper.CodeAndMsg.E05010501, e);
        } catch (CimDataEngineInfoExploreException e) {
            throw new ApiRunTimeException(EnumWrapper.CodeAndMsg.E05010502, e);
        }
        return datasetVO;
    }


    private String getDataSetPrimaryKey(DatasetVO datasetVO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (datasetVO.getLinkedPropertyTypes() != null) {
            for (PropertyTypeVO propertyTypeVO : datasetVO.getLinkedPropertyTypes()) {
                if (propertyTypeVO.getRestrictVO() != null) {
                    PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                    if (restrictVO.getPrimaryKey()) {
                        stringBuilder.append(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR).append(
                                propertyTypeVO.getPropertyTypeName());
                    }
                }
            }
        }
        return stringBuilder.toString().replaceFirst(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR, "");
    }

    private String updateDataSetPrimaryKey(DatasetDef datasetDef, DatasetVO datasetVO) {//NOSONAR
        StringBuilder primaryKeyBuilder = new StringBuilder();
        String oldKeyStr = "";
        DatasetVO datasetVO1 = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName,
                datasetDef.getDatasetRID(), true);
        if (datasetVO1 != null) {
            oldKeyStr = datasetVO1.getPrimaryKey();
        }
        String newKeyStr = getDataSetPrimaryKey(datasetVO);
        if (StringUtils.isNotBlank(oldKeyStr)) {
            String[] oldKeyArr = oldKeyStr.split(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR);
            List<String> oldKeyList = Arrays.asList(oldKeyArr);
            if (StringUtils.isBlank(newKeyStr)) {
                return null;
            } else {
                String[] newKeyArr = newKeyStr.split(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR);
                List<String> newKeyList = Arrays.asList(newKeyArr);
                //add old key
                for (String k : oldKeyList) {
                    if (newKeyList.contains(k)) {
                        primaryKeyBuilder.append(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR).append(k);
                    }
                }
                //add new key
                for (String k : newKeyList) {
                    if (!oldKeyList.contains(k)) {
                        primaryKeyBuilder.append(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR).append(k);
                    }
                }
            }
        } else {
            return newKeyStr;
        }
        return primaryKeyBuilder.toString().replaceFirst(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR, "");
    }

    public boolean deleteDataSetAndPropertyDefWithoutObjectType(String userId, String tenantId, String dataSetId) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        boolean result;
        DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
        result = datasetDefs.removeDatasetDef(dataSetId);
        if (result) {
            PropertyTypeDefCache defCache = CimCacheManager.getPropertyTypeDefCache(CimConstants.defauleSpaceName);
            defCache.removeList(dataSetId);
            CacheUtil.removeDatasetVo(CimConstants.defauleSpaceName, dataSetId);
        }
        return result;
    }
}
