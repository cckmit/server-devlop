package com.glodon.pcop.cimstatsvc.config;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class OrientdbPropertyConfig {
    private static Logger log = LoggerFactory.getLogger(OrientdbPropertyConfig.class);

    @Value("${my.orientdb.location}")
    private String location = "remote:10.129.27.104/";
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
    @Value("${my.orientdb.dataParallelCoefficient}")
    private String dataParallelCoefficient = "4";

    @Value("${my.orientdb.cacheEnable}")
    private String cacheEnable;
    @Value("${my.orientdb.localTimeToLiveSeconds}")
    private String localTimeToLiveSeconds;
    @Value("${my.orientdb.localOnHeapEntrySize}")
    private String localOnHeapEntrySize;
    @Value("${my.orientdb.clusterCacheEnable}")
    private String clusterCacheEnable;
    @Value("${my.orientdb.clusterTimeToLiveSeconds}")
    private String clusterTimeToLiveSeconds;
    @Value("${my.orientdb.clusterResourcePoolSizeMb}")
    private String clusterResourcePoolSizeMb;
    @Value("${my.orientdb.clusterLocation}")
    private String clusterLocation;
    @Value("${my.orientdb.clusterDefaultResourceId}")
    private String clusterDefaultResourceId;
    @Value("${my.orientdb.clusterShareResourceId}")
    private String clusterShareResourceId;
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

        map.put(PropertyHandler.CACHE_ENABLE, cacheEnable);
        map.put(PropertyHandler.LOCAL_CACHE_TIME_TO_LIVE_SECONDS, localTimeToLiveSeconds);
        map.put(PropertyHandler.LOCAL_CACHE_ON_HEAP_ENTRY_SIZE, localOnHeapEntrySize);
        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_ENABLE, clusterCacheEnable);
        map.put(PropertyHandler.CLUSTER_CACHE_TIME_TO_LIVE_SECONDS, clusterTimeToLiveSeconds);
        map.put(PropertyHandler.CLUSTER_CACHE_RESOURCE_POOL_SIZE_Mb, clusterResourcePoolSizeMb);
        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION, clusterLocation);
        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID, clusterDefaultResourceId);
        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID, clusterShareResourceId);

        CimConstants.defauleSpaceName = discoverSpace;
        log.info("Default OrientDb Space Name: {}", CimConstants.defauleSpaceName);
        for (Map.Entry<String, String> entity : map.entrySet()) {
            log.info("OrientDB configuration parameters: {}={}", entity.getKey(), entity.getValue());
        }
        PropertyHandler.map = map;
        DbExecute.dbName = CimConstants.defauleSpaceName;
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
