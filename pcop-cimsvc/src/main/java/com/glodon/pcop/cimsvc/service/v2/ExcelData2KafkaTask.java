package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ExcelData2KafkaTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(ExcelData2KafkaTask.class);

    private KafkaTemplate kafkaTemplate;
    private InputStream inputStream;
    private Map<String, String> sheetNametaskIdMap;
    private Map<String, List<PropertyMappingInputBean>> allpropertyMapping;
    private Map<String, String> objectTypeIdMap;
    private Map<String, Boolean> isUpdateMap;
    private String tenantId;


    public ExcelData2KafkaTask(KafkaTemplate kafkaTemplate, InputStream inputStream,
                               Map<String, String> sheetNametaskIdMap) {
        this.kafkaTemplate = kafkaTemplate;
        this.inputStream = inputStream;
        this.sheetNametaskIdMap = sheetNametaskIdMap;
    }

    public ExcelData2KafkaTask(String tenantId, Map<String, Boolean> isUpdateMap, KafkaTemplate kafkaTemplate,
                               InputStream inputStream,
                               Map<String, String> sheetNametaskIdMap,
                               Map<String, String> objectTypeIdMap,
                               Map<String, List<PropertyMappingInputBean>> allpropertyMapping) {
        this.kafkaTemplate = kafkaTemplate;
        this.inputStream = inputStream;
        this.sheetNametaskIdMap = sheetNametaskIdMap;
        this.allpropertyMapping = allpropertyMapping;
        this.objectTypeIdMap = objectTypeIdMap;
        this.tenantId = tenantId;
        this.isUpdateMap = isUpdateMap;
    }

    @Override
    public void run() {
        // ExcelParserUtil.sendDataToKafka(kafkaTemplate, inputStream, sheetNametaskIdMap);
        ExcelParserUtil.sendDataToKafkaV2(tenantId, kafkaTemplate, inputStream, sheetNametaskIdMap,
                objectTypeIdMap, isUpdateMap, allpropertyMapping);
    }

}
