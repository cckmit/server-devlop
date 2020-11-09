package com.glodon.pcop.cimstatsvc.config;

import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.external._IOT_REST.IOT_REST_MongoDBConnectionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class IoTConfigurations {

    private static Logger log = LoggerFactory.getLogger(IoTConfigurations.class);

    @Value("${iot.mg-db.host}")
    private String mongoDbHost;

    @Value("${iot.mg-db.port}")
    private String mongoDbPort;

    @Value("${iot.mg-db.db}")
    private String mongoDbDb;

    @Value("${iot.mg-db.user}")
    private String mongoDbUser;

    @Value("${iot.mg-db.pwd}")
    private String mongoDbPwd;

    @PostConstruct
    public void init() {
//        if (StringUtils.isNotBlank(realTimeAlram)) {
//            IOT_REST_DataSourceUtil.realTimeAlarm = realTimeAlram;
//            log.info("iot real time alram url: {}", IOT_REST_DataSourceUtil.realTimeAlarm);
//        } else {
//            log.error("iot real time alram url is empty");
//        }
//
//        if (StringUtils.isNotBlank(historicalAlram)) {
//            IOT_REST_DataSourceUtil.historicalAlarm = historicalAlram;
//            log.info("iot historical alram url: {}", IOT_REST_DataSourceUtil.historicalAlarm);
//        } else {
//            log.error("iot historical alarm url is empty");
//        }
//
//        if (StringUtils.isNotBlank(realTimeDevicesStatus)) {
//            IOT_REST_DataSourceUtil.realTimeDevicesStatus = realTimeDevicesStatus;
//            log.info("iot real time devices status url: {}", IOT_REST_DataSourceUtil.realTimeDevicesStatus);
//        } else {
//            log.error("iot real time devices status url is empty");
//        }
//
//        if (StringUtils.isNotBlank(savingAndOnlieRate)) {
//            IOT_REST_DataSourceUtil.savingRateAndOnlineRate = savingAndOnlieRate;
//            log.info("iot saving and online rate url: {}", IOT_REST_DataSourceUtil.savingRateAndOnlineRate);
//        } else {
//            log.error("iot saving and online rate url is empty");
//        }
//
//        if (StringUtils.isNotBlank(envMonitorAddress)) {
//            IOT_REST_DataSourceUtil.envMonitorAddress = envMonitorAddress;
//            log.info("iot environment monitor url: {}", IOT_REST_DataSourceUtil.envMonitorAddress);
//        } else {
//            log.error("iot environment monitor url is empty");
//        }
//
//        if (StringUtils.isNotBlank(realtimeDevicesFailures)) {
//            IOT_REST_DataSourceUtil.realtimeDevicesFailures = realtimeDevicesFailures;
//            log.info("iot real time devices filure url: {}", IOT_REST_DataSourceUtil.realtimeDevicesFailures);
//        } else {
//            log.error("iot real time devices filure url is empty");
//        }
//
//        if (StringUtils.isNotBlank(historicalDeviceStatus)) {
//            IOT_REST_DataSourceUtil.historicalDeviceStatus = historicalDeviceStatus;
//            log.info("iot historical device status url: {}", IOT_REST_DataSourceUtil.historicalDeviceStatus);
//        } else {
//            log.error("iot historical device status url is empty");
//        }

        if (StringUtils.isNotBlank(mongoDbHost)) {
            IOT_REST_MongoDBConnectionUtil.MONGODBV3_SERVER_ADDRESS_OBJ = mongoDbHost;
            log.info("iot mongo db host: {}", IOT_REST_MongoDBConnectionUtil.MONGODBV3_SERVER_ADDRESS_OBJ);
        } else {
            log.error("iot mongo db host is empty");
        }

        if (StringUtils.isNotBlank(mongoDbPort)) {
            IOT_REST_MongoDBConnectionUtil.MONGODBV3_SERVER_PORT_OBJ = mongoDbPort;
            log.info("iot mongo db port: {}", IOT_REST_MongoDBConnectionUtil.MONGODBV3_SERVER_PORT_OBJ);
        } else {
            log.error("iot mongo db port is empty");
        }

        if (StringUtils.isNotBlank(mongoDbDb)) {
            IOT_REST_MongoDBConnectionUtil.MONGODBV3_AUTHENTICATION_DB_OBJ = mongoDbDb;
            log.info("iot mongo db authentication database: {}", IOT_REST_MongoDBConnectionUtil.MONGODBV3_AUTHENTICATION_DB_OBJ);
        } else {
            log.error("iot mongo db authentication database is empty");
        }

        if (StringUtils.isNotBlank(mongoDbUser)) {
            IOT_REST_MongoDBConnectionUtil.MONGODBV3_USERNAME_OBJ = mongoDbUser;
            log.info("iot mongo db user: {}", IOT_REST_MongoDBConnectionUtil.MONGODBV3_USERNAME_OBJ);
        } else {
            log.error("iot mongo db user is empty");
        }

        if (StringUtils.isNotBlank(mongoDbPwd)) {
            IOT_REST_MongoDBConnectionUtil.MONGODBV3_PASS_WORD_OBJ = mongoDbPwd;
            log.info("iot mongo db password: {}", IOT_REST_MongoDBConnectionUtil.MONGODBV3_PASS_WORD_OBJ);
        } else {
            log.error("iot mongo db password is empty");
        }
    }


}
