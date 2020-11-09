package com.glodon.cim.config;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class OrientdbPropertyConfig {
    private static Logger log = LoggerFactory.getLogger(OrientdbPropertyConfig.class);

    @Value("${my.orientdb.location}")
    private String location = "remote:10.2.23.57/";
    @Value("${my.orientdb.account}")
    private String account = "root";
    @Value("${my.orientdb.password}")
    private String password = "wyc"; //NOSONAR
    @Value("${my.orientdb.dbType}")
    private String dbType = "graph";
    @Value("${my.orientdb.storeMode}")
    private String storeMode = "plocal";
    @Value("${my.orientdb.defaultPrefix}")
    private String defaultPrefix = "";
    @Value("${my.orientdb.discoverSpace}")
    private String discoverSpace = "test";
    @Value("${my.orientdb.connectionModel}")
    private String connectionModel = "true";
    private String dataParallelCoefficient = "4";
    @PostConstruct
    public void fieldInit() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PropertyHandler.DISCOVER_ENGINE_SERVICE_LOCATION, location);
        map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_ACCOUNT, account);
        map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_PWD, password);
        map.put(PropertyHandler.DISCOVER_SPACE_DATABASE_TYPE, dbType);
        map.put(PropertyHandler.DISCOVER_SPACE_STORAGE_MODE, storeMode);
        map.put(PropertyHandler.DISCOVER_SPACE_SHARE_CONNECTION_MODE, connectionModel);
        map.put(PropertyHandler.DISCOVER_SPACE_DATA_PARALLEL_COEFFICIENT, dataParallelCoefficient);
        CimConstants.defauleSpaceName = discoverSpace;
        log.info("Default OrientDb Space Name: {}", CimConstants.defauleSpaceName);
        for (Map.Entry<String, String> entity : map.entrySet()) {
            log.info("OrientDB configuration parameters: {}={}", entity.getKey(), entity.getValue());
        }
        PropertyHandler.map = map;
    }

    public static Map<String, String> readOrientdbConfig() throws IOException {
        Map<String, String> parameterMap = new HashMap<>();
        String configFileStr = "InfoDiscoverEngineCfg.properties";
        File configFile = new File(configFileStr);
        if (!configFile.exists()) {
            configFileStr = "BOOT-INF\\classes\\" + configFileStr;
            configFile = new File(configFileStr);
        } else {
            log.info("Read orientdb configuration from " + configFileStr);
        }

        if (configFile.exists()) {
            log.info("Read orientdb configuration from " + configFileStr);
            Properties properties = new Properties();
            properties.load(new FileInputStream(configFileStr));
            for (Object key : properties.keySet()) {
                parameterMap.put(key.toString(), properties.getProperty(key.toString()));
                log.info("OrientDb parameter: {}={}", key.toString(), properties.getProperty(key.toString()));
            }
        } else {
            log.error("Orientdb configuration file not found!");
        }
        return parameterMap;
    }

    public String getLocation() {
        return location;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getDiscoverSpace() {
        return discoverSpace;
    }

    public static void main(String[] args) {
        try {
            readOrientdbConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
