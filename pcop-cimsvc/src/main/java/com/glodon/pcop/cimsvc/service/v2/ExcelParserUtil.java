package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.KafkaExcelInputDataBean;
import com.glodon.pcop.cim.common.model.KafkaFileDataBean;
import com.glodon.pcop.cim.common.model.KafkaGisDataBean;
import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ExcelParserUtil {
    private static Logger log = LoggerFactory.getLogger(ExcelParserUtil.class);

    @Autowired
    private ObjectMapper objectMapper;

    private static String excelDataTopic = "excel_data";
    // private static String excelDataTopic = "send_data";
    private static String excelStatusTopic = "excel_status";


    public static List<FileStructBean> excelParser(InputStream inputStream, String fileName) throws IOException {
        log.info("Parser fexcel structure: file name={}", fileName);
        List<FileStructBean> fileStructBeanList = new ArrayList<>();
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();

        Iterator<Sheet> sheetIterator = wb.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            FileStructBean structBean = new FileStructBean();
            structBean.setSingleFileName(sheet.getSheetName());
            log.info("Parser fexcel structure: sheet name={}", sheet.getSheetName());
            structBean.setTotalCount(sheet.getLastRowNum());
            log.info("Parser fexcel structure: total row count={}", sheet.getLastRowNum());
            Row firstRow = sheet.getRow(0);
            Map<String, String> fieldTypeMap = new HashMap<>();
            if (firstRow != null) {
                log.info("Parser fexcel structure: total column count={}", firstRow.getLastCellNum());
                for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                    Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    fieldTypeMap.put(formatter.formatCellValue(cell), "STRING");
                }
            }
            log.info("Parser fexcel structure: struct={}", fieldTypeMap);
            structBean.setStruct(fieldTypeMap);

            fileStructBeanList.add(structBean);
        }
        return fileStructBeanList;
    }

    public static void sendDataToKafka(KafkaTemplate kafkaTemplate, InputStream inputStream,
                                       Map<String, String> sheetsNames) {//NOSONAR
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            int count = 0;
            String taskId = null;
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                log.info("statrt to parser sheet of {}", sheet.getSheetName());
                if (sheetsNames.containsKey(sheet.getSheetName())) {
                    taskId = sheetsNames.get(sheet.getSheetName());
                    Iterator<Row> rowIterator = sheet.rowIterator();
                    // Map<Integer, String> keyMap = new HashMap<>();
                    String[] keys = new String[1];
                    while (rowIterator.hasNext()) {
                        count = count + 1;
                        Row oneRow = rowIterator.next();
                        if (oneRow.getRowNum() == 0) {
                            keys = new String[oneRow.getLastCellNum() + 1];
                            for (int i = 0; i < oneRow.getLastCellNum(); i++) {
                                Cell cell = oneRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                                log.info("key{}={}", i, formatter.formatCellValue(cell, evaluator));
                                keys[i] = formatter.formatCellValue(cell, evaluator);
                            }
                        } else {
                            KafkaFileDataBean kafkaFileDataBean = new KafkaFileDataBean();
                            kafkaFileDataBean.setTaskId(taskId);
                            Map<String, String> dataMap = new HashMap<>();
                            for (int i = 0; i < oneRow.getLastCellNum(); i++) {
                                Cell cell = oneRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                                log.debug("cell type: {}", cell.getCellType());
                                if (i < keys.length && StringUtils.isNotBlank(keys[i])) {
                                    dataMap.put(keys[i], formatter.formatCellValue(cell, evaluator));
                                } else {
                                    log.info("no key ...");
                                    log.info("key length={}, index={}, key={}", keys.length, i, keys[i]);
                                }
                            }
                            kafkaFileDataBean.setData(dataMap);
                            log.info("===excel_data: {}", objectMapper.writeValueAsString(kafkaFileDataBean));
                            kafkaTemplate.send("excel_data", objectMapper.writeValueAsString(kafkaFileDataBean));
                        }
                    }
                } else {
                    log.info("sheet of {} should not parser", sheet.getSheetName());
                }
            }
            Map map = new HashMap();
            map.put("count", count + "");
            map.put("task_id", taskId);

            kafkaTemplate.send("excel_status", objectMapper.writeValueAsString(map));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendDataToKafkaV2(String tenantId,
                                         KafkaTemplate kafkaTemplate,
                                         InputStream inputStream,
                                         Map<String, String> sheetsNames,
                                         Map<String, String> objectTypeIdMap,
                                         Map<String, Boolean> isUpdateMap,
                                         Map<String, List<PropertyMappingInputBean>> propertyMapping) {//NOSONAR
        ObjectMapper objectMapper = new ObjectMapper();
        log.debug("object type id map: [{}]", objectTypeIdMap);
        log.debug("is update map: [{}]", isUpdateMap);
        log.debug("property keys map: [{}]", JSON.toJSONString(propertyMapping));
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Iterator<Sheet> sheetIterator = wb.sheetIterator();
            int count = 0;
            String taskId = null;
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                log.info("statrt to parser sheet of {}", sheet.getSheetName());
                if (sheetsNames.containsKey(sheet.getSheetName())) {
                    taskId = sheetsNames.get(sheet.getSheetName());
                    boolean isUpdate = isUpdateMap.get(sheet.getSheetName());
                    Iterator<Row> rowIterator = sheet.rowIterator();
                    String[] keys = new String[1];
                    String objectTypeId = objectTypeIdMap.get(sheet.getSheetName());
                    while (rowIterator.hasNext()) {
                        count = count + 1;
                        Row oneRow = rowIterator.next();
                        if (oneRow.getRowNum() == 0) {
                            keys = new String[oneRow.getLastCellNum() + 1];
                            for (int i = 0; i < oneRow.getLastCellNum(); i++) {
                                Cell cell = oneRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                                log.info("key{}={}", i, formatter.formatCellValue(cell, evaluator));
                                keys[i] = formatter.formatCellValue(cell, evaluator);
                            }
                            keys = parserPropertiesMapping(sheet.getSheetName(), propertyMapping, keys);
                            log.debug("final import data keys: [{}]", JSON.toJSONString(keys));
                        } else {
                            // KafkaFileDataBean kafkaFileDataBean = new KafkaFileDataBean();
                            KafkaExcelInputDataBean kafkaExcelInputDataBean = new KafkaExcelInputDataBean();
                            kafkaExcelInputDataBean.setTenantId(tenantId);
                            kafkaExcelInputDataBean.setObjectName(objectTypeId);
                            kafkaExcelInputDataBean.setUpdate(isUpdate);

                            kafkaExcelInputDataBean.setTaskId(Long.valueOf(taskId));
                            Map<String, Object> dataMap = new HashMap<>();
                            for (int i = 0; i < oneRow.getLastCellNum(); i++) {
                                Cell cell = oneRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                                if (i < keys.length && StringUtils.isNotBlank(keys[i])) {
                                    dataMap.put(keys[i], formatter.formatCellValue(cell, evaluator));
                                    log.debug("cell value: {}", formatter.formatCellValue(cell, evaluator));
                                } else {
                                    log.debug("no key mapping: key length={}, index={}, key={}", keys.length, i,
                                            keys[i]);
                                }
                            }
                            log.debug("data map: [{}]", dataMap);
                            kafkaExcelInputDataBean.setData(dataMap);
                            log.debug("===excel_data: {}", objectMapper.writeValueAsString(kafkaExcelInputDataBean));
                            kafkaTemplate.send(excelDataTopic,
                                    objectMapper.writeValueAsString(kafkaExcelInputDataBean));
                        }
                    }
                } else {
                    log.info("sheet of {} should not parser", sheet.getSheetName());
                }
            }
            Map map = new HashMap();
            map.put("count", count + "");
            map.put("taskId", taskId);
            map.put("tenantId", tenantId);
            kafkaTemplate.send(excelStatusTopic, objectMapper.writeValueAsString(map));
        } catch (IOException e) {
            log.error("import failed", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.debug("===finish===");
    }

    private static String[] parserPropertiesMapping(String sheetName,
                                                    Map<String, List<PropertyMappingInputBean>> propertyMapping,
                                                    String[] headers) {
        Map<String, String> tmpKeyMap = new HashMap<>();
        String[] keyMap = new String[headers.length];

        if (propertyMapping.containsKey(sheetName)) {
            List<PropertyMappingInputBean> beanList = propertyMapping.get(sheetName);
            if (beanList != null && beanList.size() > 0) {
                for (PropertyMappingInputBean inputBean : beanList) {
                    if (StringUtils.isNotBlank(inputBean.getDesPropertyName())) {
                        tmpKeyMap.put(inputBean.getSrcPropertyName(), inputBean.getDesPropertyName());
                    } else if (StringUtils.isNotBlank(inputBean.getSrcPropertyName())) {
                        tmpKeyMap.put(inputBean.getSrcPropertyName(),
                                PinyinUtils.getPinYinWithoutSpecialChar(inputBean.getSrcPropertyName()));
                    }
                }
            } else {
                for (String head : headers) {
                    tmpKeyMap.put(head, PinyinUtils.getPinYinWithoutSpecialChar(head));
                }
            }
        } else {
            for (String head : headers) {
                tmpKeyMap.put(head, PinyinUtils.getPinYinWithoutSpecialChar(head));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            keyMap[i] = tmpKeyMap.get(headers[i]);
        }

        return keyMap;
    }

}
