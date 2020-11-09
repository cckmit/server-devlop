package com.glodon.pcop.cimsvc.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendMessageUtil {
    private static final Logger log = LoggerFactory.getLogger(SendMessageUtil.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${cim.kafka.bim-translate-topic}")
    private String translateTopic;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(BimFileUploadTranslateBean bean) {
        // BimFileUploadTranslateBean bean = new BimFileUploadTranslateBean(bucket, fileName, objectId);
        try {
            kafkaTemplate.send(translateTopic, objectMapper.writeValueAsString(bean));
        } catch (JsonProcessingException e) {
            log.error("send bim translate message to kafka failed", e);
        }
    }

    public void setTranslateTopic(String translateTopic) {
        this.translateTopic = translateTopic;
    }
}
