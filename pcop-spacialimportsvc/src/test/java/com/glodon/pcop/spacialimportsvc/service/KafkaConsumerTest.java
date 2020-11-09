package com.glodon.pcop.spacialimportsvc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.KafkaDataBean;
import com.glodon.pcop.cim.common.model.KafkaGisDataBean;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tangd-a
 * @title: KafkaConsumerTest
 * @projectName pcop-cim-server
 * @date 2019/5/1615:17
 */
// @RunWith(SpringRunner.class)
// @SpringBootTest
@Ignore
public class KafkaConsumerTest {
    @Autowired
    private KafkaTemplate template;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\"data\":{\"ssdk\":\"103地块\",\"xmmc\":\"安置房一期\",\"ssbd\":\"一标段\",\"sspq\":\"榆垡片区\"," +
                "\"ldmc\":\"103-1#楼\"},\"task_id\":\"1905166300717\"}";
        for (int i = 0; i < 10; i++) {
            template.send("topic.excel_data", json);
        }
        Map map = new HashMap<>();
        map.put("count", "10");
        map.put("task_id", "123123");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(map);
        template.send("topic.excel_status", s);

    }

    @Test
    public void test2() throws JsonProcessingException {

        String json = "{\"taskId\":1908296302040,\"objectName\":\"xianzhuangcigaoyaAguanxian\",\"tenantId\":\"3\"," +
                "\"data\":{\"qiyuan\":\"罗奇营高压A站\",\"shusongyali\":\"PN1.6MPa\",\"mingcheng\":\"京开高速次高压A管线\"," +
                "\"guanjing\":\"DN500\"},\"isUpdate\":false}";

        KafkaGisDataBean gisDataBean = JSON.parseObject(json, KafkaGisDataBean.class);

        System.out.println("taskId=" + gisDataBean.getTaskId());
        System.out.println("objectName=" + gisDataBean.getObjectName());
        System.out.println("data=" + gisDataBean.getData());
        System.out.println("jobEnd=" + gisDataBean.getJobEnd());

    }


    @Test
    public void test3() throws Exception{
        String msg = "{\"count\":\"7\",\"tenantId\":\"3\",\"taskId\":\"1909176305374\"}";
        KafkaDataBean kafkaDataBean = objectMapper.readValue(msg, KafkaDataBean.class);

        System.out.println(kafkaDataBean);
    }


}