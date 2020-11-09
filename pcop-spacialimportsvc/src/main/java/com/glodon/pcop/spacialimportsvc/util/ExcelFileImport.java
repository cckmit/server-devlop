package com.glodon.pcop.spacialimportsvc.util;

import com.glodon.pcop.cim.common.model.FileImportTaskInputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper.DATA_SET_STRUCTURE;
import com.glodon.pcop.cim.common.util.EnumWrapper.DATA_SET_TYPE;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileImport {
    private static Logger log = LoggerFactory.getLogger(ExcelFileImport.class);

    /**
     * 解析excel文件结构
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Map<String, String> getExcelFileStructure(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("{} file not exists", filePath);
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();
        Sheet firstSheet = wb.getSheetAt(0);
        Map<String, String> fieldTypeMap = new HashMap<>();
        if (firstSheet != null) {
            Row firstRow = firstSheet.getRow(0);
            if (firstRow != null) {
                for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                    Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    fieldTypeMap.put(formatter.formatCellValue(cell), "STRING");
                }
            }
            log.info("Excel structure: {}", fieldTypeMap);
        }
        return fieldTypeMap;
    }

    public static List<Map<String, Object>> readExcelContent(String filePath) throws IOException {
        List<Map<String, Object>> contents = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();
        Sheet firstSheet = wb.getSheetAt(0);

        List<String> headerList = new ArrayList<>();
        int columnNumber = firstSheet.getRow(0).getLastCellNum();
        Row firstRow = firstSheet.getRow(0);
        for (int i = 0; i < columnNumber; i++) {
            headerList.add(formatter.formatCellValue(firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)));
        }

        //暂时只导入第一个sheet
        if (firstSheet != null) {
            //从第二行开始导入，第一行为标题
            for (int i = 1; i <= firstSheet.getLastRowNum(); i++) {
                Row row = firstSheet.getRow(i);
                if (row != null) {
                    Map<String, Object> fieldTypeMap = new HashMap<>();
                    for (int n = 0; n < columnNumber; n++) {
                        Cell cell = row.getCell(n, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        fieldTypeMap.put(headerList.get(n), formatter.formatCellValue(cell));
                    }
                    contents.add(fieldTypeMap);
                }
            }
        }
        return contents;
    }

    public void excelDataImport(FileImportTaskInputBean taskInputBean) throws IOException, DataServiceModelRuntimeException {
        if (DATA_SET_TYPE.INSTANCE.equals(taskInputBean.getDataSetType())) {
            List<Map<String, Object>> contents = readExcelContent(taskInputBean.getFileName());
            Map<String, String> structure = getExcelFileStructure(taskInputBean.getFileName());
            if (DATA_SET_STRUCTURE.COLLECTION.equals(taskInputBean.getDataSetStructure())) {
                ImportCommonUtil.instanceCollectionLoader(taskInputBean, structure, contents);
            } else if (DATA_SET_STRUCTURE.SINGLE.equals(taskInputBean.getDataSetStructure())) {
                if (taskInputBean.getIsMapping()) {
                    ImportCommonUtil.instanceMappingSingleLoader(taskInputBean, contents);
                } else {
                    log.error("single data sets no mapping is not support");
                }
            } else {
                log.error("data structure is not support: {}", taskInputBean.getDataSetStructure());
            }
        } else if (DATA_SET_TYPE.OBJECT.equals(taskInputBean.getDataSetType())) {
            log.error("object type import not support currently");
        } else {
            log.error("data set type is not support: {}", taskInputBean.getDataSetType());
        }
    }

    // /**
    //  * 实例，非匹配/匹配，集合属性集，数据导入
    //  *
    //  * @param taskInputBean
    //  * @param structureMap
    //  * @param contents
    //  * @return
    //  * @throws DataServiceModelRuntimeException
    //  */
    // public static List<String> instanceCollectionLoader(FileImportTaskInputBean taskInputBean, Map<String, String> structureMap, List<Map<String, Object>> contents) throws DataServiceModelRuntimeException {
    //     Map<String, String> keyMapping = new HashMap<>();
    //     String dataSetName;
    //     if (taskInputBean.getIsMapping()) {
    //         List<DataSetMapping> dataSetMappings = taskInputBean.getDataSetMappings();
    //         if (dataSetMappings == null || dataSetMappings.size() != 1) {
    //             log.error("collection data set mapping load: must be one data set mapping");
    //             return null;
    //         }
    //         keyMapping = ImportCommonUtil.getKeyMapping(taskInputBean.getDataSetMappings());
    //         if (keyMapping == null || keyMapping.size() == 0) {
    //             log.error("mapping loader, but no property mapping is provided");
    //             return null;
    //         }
    //         dataSetName = dataSetMappings.get(0).getDataSetName();
    //     } else {
    //         if (!ImportCommonUtil.createDataSet(taskInputBean, structureMap)) {
    //             log.error("create data set failed");
    //             return null;
    //         }
    //         dataSetName = taskInputBean.getDataSetName();
    //     }
    //
    //     CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, taskInputBean.getTenantId());
    //     InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(taskInputBean.getObjectTypeId());
    //     if (infoObjectDef == null) {
    //         log.error("infoObjectDef of {} is null", taskInputBean.getObjectTypeId());
    //         return null;
    //     }
    //
    //     if (StringUtils.isBlank(taskInputBean.getInstanceRid())) {
    //         log.error("instanceRid is blank");
    //         return null;
    //     }
    //
    //     InfoObject infoObject = infoObjectDef.getObject(taskInputBean.getInstanceRid());
    //     if (infoObject == null) {
    //         log.error("no instance of objectTypeId={}, instanceRid={}", taskInputBean.getObjectTypeId(), taskInputBean.getInstanceRid());
    //         return null;
    //     }
    //
    //     Dataset dataset = infoObject.getCollectionDataset(dataSetName);
    //     if (dataset == null) {
    //         log.error("data set is null, objectTypeId={}, instanceRid={}, dataSetName={}", taskInputBean.getObjectTypeId(), taskInputBean.getInstanceRid(), taskInputBean.getDataSetName());
    //         return null;
    //     }
    //
    //     /*List<Map<String, Object>> contents = readExcelContent(filePath);
    //     if (contents == null || contents.size() == 0) {
    //         log.error("no content file of {}", filePath);
    //         return null;
    //     }*/
    //
    //     List<Map<String, Object>> formalContents = new ArrayList<>();
    //     if (taskInputBean.getIsMapping()) {
    //         //mapping data process
    //         for (Map<String, Object> valueMap : contents) {
    //             Map<String, Object> tmpMap = new HashMap<>();
    //             for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
    //                 tmpMap.put(entry.getValue(), valueMap.get(entry.getKey()));
    //             }
    //             formalContents.add(tmpMap);
    //         }
    //     } else {
    //         formalContents = contents;
    //     }
    //
    //     BatchDataOperationResult operationResult = dataset.loadObjectDataset(formalContents);
    //     log.info("load result: \n{}", (new Gson()).toJson(operationResult));
    //     return operationResult.getSuccessDataInstanceRIDs();
    // }

    // /**
    //  * 实例，匹配，单值属性集，数据导入
    //  *
    //  * @param taskInputBean
    //  * @param contents
    //  * @return
    //  * @throws DataServiceModelRuntimeException
    //  */
    // public static List<String> instanceMappingSingleLoader(FileImportTaskInputBean taskInputBean, List<Map<String, Object>> contents) throws DataServiceModelRuntimeException {
    //     Map<String, String> keyMapping = ImportCommonUtil.getKeyMapping(taskInputBean.getDataSetMappings());
    //     if (keyMapping == null || keyMapping.size() == 0) {
    //         log.error("mapping loader, but no property mapping is provided");
    //         return null;
    //     } else {
    //         log.info("property key mapping: {}", keyMapping);
    //     }
    //
    //     CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, taskInputBean.getTenantId());
    //     InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(taskInputBean.getObjectTypeId());
    //     if (infoObjectDef == null) {
    //         log.error("infoObjectDef of {} is null", taskInputBean.getObjectTypeId());
    //         return null;
    //     }
    //
    //     //若已经定义复合主键，则使用复合主键
    //     Set<String> primaryKeys = ImportCommonUtil.getPrimaryKeyPropertyName(infoObjectDef);
    //     log.info("primary keys: {}", (new Gson()).toJson(primaryKeys));
    //     List<String> pks = new ArrayList<>();
    //     if (primaryKeys != null) {
    //         for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
    //             if (primaryKeys.contains(entry.getValue())) {
    //                 pks.add(entry.getKey());
    //             }
    //         }
    //     }
    //
    //     //若未定义复合主键，则ID属性必须被匹配
    //     if (primaryKeys != null && primaryKeys.size() > 0 && pks.size() == primaryKeys.size()) {
    //         log.info("all single data set primary key are mapped");
    //     } else {
    //         for (Map.Entry<String, String> entry : keyMapping.entrySet()) {
    //             if (entry.getValue().equals(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME)) {
    //                 // primaryKeys.add(entry.getKey());
    //                 pks.add(entry.getKey());
    //                 log.info("ID property is mapped");
    //             }
    //         }
    //     }
    //
    //     if (pks.size() == 0) {
    //         log.error("all single data set primary key are mapped or ID primary key is mapped");
    //         return null;
    //     }
    //
    //     //保持复合主键有序
    //     Collections.sort(pks);
    //
    //     List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
    //     for (Map<String, Object> valuesMap : contents) {
    //         Map<String, Object> baseValuesMap = new HashMap<>();
    //         StrBuilder strBuilder = new StrBuilder();
    //         for (int i = 0; i < pks.size(); i++) {
    //             strBuilder.append(valuesMap.get(pks.get(i)).toString()).append(ImportCimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR);
    //         }
    //         baseValuesMap.put(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME, strBuilder.toString());
    //
    //         Map<String, Object> generalValuesMap = new HashMap<>();
    //         for (String srcKey : keyMapping.keySet()) {
    //             generalValuesMap.put(keyMapping.get(srcKey), valuesMap.get(srcKey));
    //         }
    //
    //         InfoObjectValue infoObjectValue = new InfoObjectValue();
    //         infoObjectValue.setBaseDatasetPropertiesValue(baseValuesMap);
    //         infoObjectValue.setGeneralDatasetsPropertiesValue(generalValuesMap);
    //         infoObjectValueList.add(infoObjectValue);
    //     }
    //
    //     BatchDataOperationResult operationResult = infoObjectDef.newObjects(infoObjectValueList, false);
    //     log.info("load result: \n{}", (new Gson()).toJson(operationResult));
    //     return operationResult.getSuccessDataInstanceRIDs();
    // }

}
