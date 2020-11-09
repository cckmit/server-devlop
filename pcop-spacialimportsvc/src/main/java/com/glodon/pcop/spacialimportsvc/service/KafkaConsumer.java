package com.glodon.pcop.spacialimportsvc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.KafkaDataBean;
import com.glodon.pcop.cim.common.model.KafkaGisDataBean;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.spacialimportsvc.exception.InfoObjectNotFoundException;
import com.glodon.pcop.spacialimportsvc.schedule.BimfaceStatus;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class KafkaConsumer {
    private static Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private SaveKafkaFileData saveKafkaFileData;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private BimfaceStatus bimfaceStatus;

    @Value("${cim.kafka.topic.gis-data}")
    private String gisDataTopic;

    @PostConstruct
    public void init() {
        log.info("===kafka gis data topic: [{}]", gisDataTopic);
    }


    // @KafkaListener(topics = {"excel_data"})
    @KafkaListener(topics = {"${cim.kafka.topic.excel-data}"})
    public void consumerGisState(ConsumerRecord<?, ?> record) {
        log.debug("===excel_data, topic = {}, msg = {}", record.topic(), record.value());
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            saveKafkaFileData.createOrUpdateGisData(cds, record.value().toString());
        } catch (InfoObjectNotFoundException e) {
            if (cds != null) {
                cds.closeSpace();
            }
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            try {
                saveKafkaFileData.createOrUpdateGisData(cds, record.value().toString());
            } catch (Exception ex) {
                log.error("save excel data failed: [{}]", record.value());
                log.error("save excel data failed", e);
            }
        } catch (CimDataEngineInfoExploreException | CimDataEngineRuntimeException | DataServiceUserException e) {
            log.error("save excel data failed: [{}]", record.value());
            log.error("save excel data failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    // @KafkaListener(topics = {"${cim.kafka.gis-data-v1}"})
    public void consumerGisDataV1(ConsumerRecord<?, ?> record) {
        log.debug("===gis_data, topic = {}, msg = {}", record.topic(), record.value());
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            saveKafkaFileData.saveGisData(cds, record.value().toString());
        } catch (Exception e) {
            log.error("save gis data failed: [{}]", record.value());
            log.error("save gis data failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @KafkaListener(topics = {"${cim.kafka.topic.gis-data}"})
    public void consumerGisData(ConsumerRecord<?, ?> record) {
        log.debug("===gis_data, topic = {}, msg = {}", record.topic(), record.value());
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            saveKafkaFileData.createOrUpdateGisData(cds, record.value().toString());
        } catch (Exception e) {
            log.error("save gis data failed: [{}]", record.value());
            log.error("save gis data failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    // @KafkaListener(topics = "gis_status")
    @KafkaListener(topics = {"${cim.kafka.topic.gis-status}"})
    public void listenGisStatus(ConsumerRecord<?, ?> record) {
        log.info("===gis_status, topic = {}, msg = {}", record.topic(), record.value());
        //构造出符合gis_data的json
        String message = record.value().toString();
        KafkaDataBean kfData = JSON.parseObject(message, KafkaDataBean.class);

        String taskId = kfData.getTaskId();
        String tenantId = kfData.getTenantId();
        KafkaGisDataBean kafkaGisDataBean = new KafkaGisDataBean();
        kafkaGisDataBean.setTaskId(Long.valueOf(taskId));
        kafkaGisDataBean.setTenantId(tenantId);
        kafkaGisDataBean.setJobEnd(true);
        String kafkaFileDataBeanJson = JSON.toJSONString(kafkaGisDataBean);
        //使用ProducerRecord发送消息
        ProducerRecord producerRecord = new ProducerRecord(gisDataTopic, kafkaFileDataBeanJson);
        kafkaTemplate.send(producerRecord);
    }


    // @KafkaListener(topics = "excel_status")
    @KafkaListener(topics = {"${cim.kafka.topic.excel-status}"})
    public void listenExcelStatusV2(ConsumerRecord<?, ?> record) {
        log.info("===excel_status, topic = {}, msg = {}", record.topic(), record.value());
        //构造出符合excel_data的json
        String message = record.value().toString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KafkaDataBean kfData = objectMapper.readValue(message, KafkaDataBean.class);
            String taskId = kfData.getTaskId();
            String tenantId = kfData.getTenantId();
            KafkaGisDataBean kafkaGisDataBean = new KafkaGisDataBean();
            kafkaGisDataBean.setTaskId(Long.valueOf(taskId));
            kafkaGisDataBean.setTenantId(tenantId);
            kafkaGisDataBean.setJobEnd(true);
            String kafkaFileDataBeanJson = objectMapper.writeValueAsString(kafkaGisDataBean);
            //使用ProducerRecord发送消息
            ProducerRecord producerRecord = new ProducerRecord("excel_data", kafkaFileDataBeanJson);
            kafkaTemplate.send(producerRecord);
        } catch (IOException e) {
            log.error("json format Incorrect");
        }
    }

    @KafkaListener(topics = {"${cim.kafka.topic.bim-translate}"})
    public void listenBimTranslate(ConsumerRecord<?, ?> record) {
        log.info("===bim translate: {}===", record.value().toString());
        bimfaceStatus.uploadAndTranslate(JSON.parseObject(record.value().toString(), BimFileUploadTranslateBean.class));
    }

}
