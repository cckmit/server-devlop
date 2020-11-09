package com.glodon.pcop.spacialimportsvc.util;

import com.glodon.pcop.cim.common.model.DataSetMapping;
import com.glodon.pcop.cim.common.model.FileImportTaskInputBean;
import com.glodon.pcop.cim.common.model.PropertyMapping;
import com.glodon.pcop.cim.common.service.MinioService;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.Dataset;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.spacialimportsvc.config.OrientdbPropertyConfig;
import com.google.gson.Gson;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImportCommonUtil {
    private static Logger log = LoggerFactory.getLogger(ImportCommonUtil.class);


    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }

    /**
     * 返回shp文件路径，若不存在则从minio下载
     *
     * @param fileId
     * @return
     * @throws InvalidEndpointException
     * @throws InvalidPortException
     */
    public static String getZipFilePath(String fileId) throws InvalidEndpointException, InvalidPortException, IOException {
        String tempPathStr = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_FILE_PATH);
        String url = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_URL);
        String userName = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_USERNAME);
        String pwd = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_PASSWORD);
        String bucket = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_BUCKET);

        Path tempDir = Paths.get(tempPathStr);
        Files.createDirectories(tempDir);
        Path filePath = tempDir.resolve(fileId);
        String filePathStr = filePath.toAbsolutePath().toString();
        log.info("Local file {}", filePathStr);
        if (!Files.exists(filePath)) {
            log.info("Minio file download: url={}, userName={}, pass_word={}, bucket={}", url, userName, pwd, bucket);
            MinioService minioService = new MinioService(new MinioClient(url, userName, pwd), bucket);
            minioService.fileDownload(fileId, filePathStr);
        }
        return filePathStr;
    }

    public static String getShpFilePath(String fileId, String LocalTempPath) throws InvalidPortException, InvalidEndpointException, IOException {
        String zipFile = getZipFilePath(fileId);
        log.info("unzip shp file {} to {}", zipFile, LocalTempPath);
        ZipFileUtil.unzip(zipFile, LocalTempPath);
        File file = new File(LocalTempPath);
        for (File outFile : file.listFiles()) {
            if (outFile.isDirectory()) {
                for (File inFile : outFile.listFiles()) {
                    if (inFile.getName().endsWith(".shp")) {
                        return inFile.getAbsolutePath();
                    }
                }
            } else {
                if (outFile.getName().endsWith(".shp")) {
                    return outFile.getAbsolutePath();
                }
            }
        }
        throw new RuntimeException("shp file is not found");
    }


    /**
     * 合并不同属性集中的属性匹配，并去重
     *
     * @param dataSetMappings
     * @return
     */
    public static List<PropertyMapping> mergePropertyMapping(List<DataSetMapping> dataSetMappings) {//NOSONAR
        List<PropertyMapping> propertyMappings = new ArrayList<>();
        Set<String> propertyRids = new HashSet<>();

        if (dataSetMappings != null && dataSetMappings.size() > 0) {
            for (DataSetMapping dataSetMapping : dataSetMappings) {
                List<PropertyMapping> tmpPropertyMappings = dataSetMapping.getPropertyMappings();
                if (tmpPropertyMappings != null) {
                    for (PropertyMapping pm : tmpPropertyMappings) {
                        if (!propertyRids.contains(pm.getPropertyRid())) {
                            propertyRids.add(pm.getPropertyRid());
                            propertyMappings.add(pm);
                        }
                    }
                }
            }
        }
        return propertyMappings;
    }


    /**
     * 创建属性集
     *
     * @param taskInputBean
     * @param properties
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public static boolean createDataSet(FileImportTaskInputBean taskInputBean, Map<String, String> properties) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, taskInputBean.getTenantId());
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(taskInputBean.getObjectTypeId());
        if (infoObjectDef == null) {
            log.error("info object type of {} not found", taskInputBean.getObjectTypeId());
            return false;
        }

        DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
        if (datasetDefs == null) {
            log.error("Can not get dataSetDefs");
            return false;
        }

        DatasetVO datasetVO = new DatasetVO();
        datasetVO.setDatasetName(taskInputBean.getDataSetName());
        datasetVO.setDatasetDesc(taskInputBean.getDataSetName());
        datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.valueOf(taskInputBean.getDataSetType().toString()));
        datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.valueOf(taskInputBean.getDataSetStructure().toString()));
        datasetVO.setDatasetClassify(taskInputBean.getDataType().toString());
        //add data set definition
        DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
        if (datasetDef == null) {
            log.error("add data set failed");
            return false;
        }

        PropertyTypeDefs propertyTypeDefs = cimModelCore.getPropertyTypeDefs();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            PropertyTypeVO propertyTypeVO = new PropertyTypeVO();
            propertyTypeVO.setPropertyTypeName(entry.getKey());
            propertyTypeVO.setPropertyFieldDataClassify("STRING");
            propertyTypeVO.setPropertyTypeDesc(entry.getKey());
            //add property definition
            PropertyTypeDef tmpPropertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
            //link property with data set
            if (tmpPropertyTypeDef != null) {
                datasetDef.addPropertyTypeDef(tmpPropertyTypeDef.getPropertyTypeRID());
            } else {
                log.error("add property definition failed");
            }
        }

        if (taskInputBean.getDataSetType().equals(EnumWrapper.DATA_SET_TYPE.INSTANCE)) {
            infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
        } else if (taskInputBean.getDataSetType().equals(EnumWrapper.DATA_SET_TYPE.OBJECT)) {
            infoObjectDef.addDefinitionDatasetDef(datasetDef.getDatasetRID());
        } else {
            log.error("data set type of {} is unsupport", taskInputBean.getObjectTypeId());
            return false;
        }

        return true;
    }


    /**
     * @param dataSetMappings key=source property name, value=target property name
     * @return
     */
    public static Map<String, String> getKeyMapping(List<DataSetMapping> dataSetMappings) {
        Map<String, String> keyMapping = new HashMap<>();
        List<PropertyMapping> propertyMappings = mergePropertyMapping(dataSetMappings);
        if (propertyMappings != null && propertyMappings.size() > 0) {
            for (PropertyMapping mapping : propertyMappings) {
                keyMapping.put(mapping.getSourcePropertyName(), mapping.getTargetPropertyName());
            }
        }
        return keyMapping;
    }


    /**
     * 获取所有single属性集的主键属性名称
     *
     * @param infoObjectDef
     * @return
     */
    public static Set<String> getPrimaryKeyPropertyName(InfoObjectDef infoObjectDef) {//NOSONAR
        Set<String> primaryKeyProperties = new HashSet<>();

        List<DatasetDef> datasetDefList = infoObjectDef.getDatasetDefs();
        if (datasetDefList != null && datasetDefList.size() > 0) {
            for (DatasetDef datasetDef : datasetDefList) {
                if (!datasetDef.isCollectionDataset()) {
                    // DatasetVO datasetVO = datasetDef.getDataSetVO(true);
                    DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(ImportCimConstants.defauleSpaceName, datasetDef.getDatasetRID(), true);
                    if (datasetVO != null) {
                        List<PropertyTypeVO> propertyTypeVOList = datasetVO.getLinkedPropertyTypes();
                        if (propertyTypeVOList != null && propertyTypeVOList.size() > 0) {
                            for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                                PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                                if (restrictVO != null && restrictVO.getPrimaryKey()) {
                                    primaryKeyProperties.add(propertyTypeVO.getPropertyTypeName());
                                }
                            }
                        }
                    }
                }
            }
        }

        return primaryKeyProperties;
    }

    /**
     * 实例，非匹配/匹配，集合属性集，数据导入
     *
     * @param taskInputBean
     * @param structureMap
     * @param contents
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public static List<String> instanceCollectionLoader(FileImportTaskInputBean taskInputBean, Map<String, String> structureMap, List<Map<String, Object>> contents) throws DataServiceModelRuntimeException {//NOSONAR
        Map<String, String> keyMapping = new HashMap<>();
        String dataSetName;
        if (taskInputBean.getIsMapping()) {
            List<DataSetMapping> dataSetMappings = taskInputBean.getDataSetMappings();
            if (dataSetMappings == null || dataSetMappings.size() != 1) {
                log.error("collection data set mapping load: must be one data set mapping");
                return null;
            }
            keyMapping = ImportCommonUtil.getKeyMapping(taskInputBean.getDataSetMappings());
            if (keyMapping == null || keyMapping.size() == 0) {
                log.error("mapping loader, but no property mapping is provided");
                return null;
            }
            dataSetName = dataSetMappings.get(0).getDataSetName();
        } else {
            if (!ImportCommonUtil.createDataSet(taskInputBean, structureMap)) {
                log.error("create data set failed");
                return null;
            }
            dataSetName = taskInputBean.getDataSetName();
        }

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, taskInputBean.getTenantId());
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(taskInputBean.getObjectTypeId());
        if (infoObjectDef == null) {
            log.error("infoObjectDef of {} is null", taskInputBean.getObjectTypeId());
            return null;
        }

        if (StringUtils.isBlank(taskInputBean.getInstanceRid())) {
            log.error("instanceRid is blank");
            return null;
        }

        InfoObject infoObject = infoObjectDef.getObject(taskInputBean.getInstanceRid());
        if (infoObject == null) {
            log.error("no instance of objectTypeId={}, instanceRid={}", taskInputBean.getObjectTypeId(), taskInputBean.getInstanceRid());
            return null;
        }

        Dataset dataset = infoObject.getCollectionDataset(dataSetName);
        if (dataset == null) {
            log.error("data set is null, objectTypeId={}, instanceRid={}, dataSetName={}", taskInputBean.getObjectTypeId(), taskInputBean.getInstanceRid(), taskInputBean.getDataSetName());
            return null;
        }

        /*List<Map<String, Object>> contents = readExcelContent(filePath);
        if (contents == null || contents.size() == 0) {
            log.error("no content file of {}", filePath);
            return null;
        }*/

        List<Map<String, Object>> formalContents = new ArrayList<>();
        if (taskInputBean.getIsMapping()) {
            //mapping data process
            for (Map<String, Object> valueMap : contents) {
                Map<String, Object> tmpMap = new HashMap<>();
                for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
                    tmpMap.put(entry.getValue(), valueMap.get(entry.getKey()));
                }
                formalContents.add(tmpMap);
            }
        } else {
            formalContents = contents;
        }

        BatchDataOperationResult operationResult = dataset.loadObjectDataset(formalContents);
        log.info("load result: \n{}", (new Gson()).toJson(operationResult));
        return operationResult.getSuccessDataInstanceRIDs();
    }

    /**
     * 实例，匹配，单值属性集，数据导入
     *
     * @param taskInputBean
     * @param contents
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public static List<String> instanceMappingSingleLoader(FileImportTaskInputBean taskInputBean, List<Map<String, Object>> contents) throws DataServiceModelRuntimeException {//NOSONAR
        Map<String, String> keyMapping = ImportCommonUtil.getKeyMapping(taskInputBean.getDataSetMappings());
        if (keyMapping == null || keyMapping.size() == 0) {
            log.error("mapping loader, but no property mapping is provided");
            return null;
        } else {
            log.info("property key mapping: {}", keyMapping);
        }

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, taskInputBean.getTenantId());
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(taskInputBean.getObjectTypeId());
        if (infoObjectDef == null) {
            log.error("infoObjectDef of {} is null", taskInputBean.getObjectTypeId());
            return null;
        }

        //若已经定义复合主键，则使用复合主键
        Set<String> primaryKeys = ImportCommonUtil.getPrimaryKeyPropertyName(infoObjectDef);
        log.info("primary keys: {}", (new Gson()).toJson(primaryKeys));
        List<String> pks = new ArrayList<>();
        if (primaryKeys != null) {
            for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
                if (primaryKeys.contains(entry.getValue())) {
                    pks.add(entry.getKey());
                }
            }
        }

        //若未定义复合主键，则ID属性必须被匹配
        if (primaryKeys != null && primaryKeys.size() > 0 && pks.size() == primaryKeys.size()) {
            log.info("all single data set primary key are mapped");
        } else {
            for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
                if (entry.getValue().equals(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME)) {
                    // primaryKeys.add(entry.getKey());
                    pks.add(entry.getKey());
                    log.info("ID property is mapped");
                }
            }
        }

        if (pks.size() == 0) {
            log.error("all single data set primary key are mapped or ID primary key is mapped");
            return null;
        }

        //保持复合主键有序
        Collections.sort(pks);

        List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
        for (Map<String, Object> valuesMap : contents) {
            Map<String, Object> baseValuesMap = new HashMap<>();
            StrBuilder strBuilder = new StrBuilder();
            for (int i = 0; i < pks.size(); i++) {
                strBuilder.append(valuesMap.get(pks.get(i)).toString()).append(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR);
            }
            if (strBuilder.endsWith(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR)) {
                strBuilder.deleteCharAt(strBuilder.lastIndexOf(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR));
            }
            baseValuesMap.put(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME, strBuilder.toString());

            Map<String, Object> generalValuesMap = new HashMap<>();
            for (String srcKey : keyMapping.keySet()) {
                generalValuesMap.put(keyMapping.get(srcKey), valuesMap.get(srcKey));
            }

            InfoObjectValue infoObjectValue = new InfoObjectValue();
            infoObjectValue.setBaseDatasetPropertiesValue(baseValuesMap);
            infoObjectValue.setGeneralDatasetsPropertiesValue(generalValuesMap);
            infoObjectValueList.add(infoObjectValue);
        }

        BatchDataOperationResult operationResult = infoObjectDef.newObjects(infoObjectValueList, false);
        log.info("load result: \n{}", (new Gson()).toJson(operationResult));
        return operationResult.getSuccessDataInstanceRIDs();
    }


}
