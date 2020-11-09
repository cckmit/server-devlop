package com.glodon.pcop.spacialimportsvc.config;

import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class OrientdbPropertyConfig {
    private static Logger log = LoggerFactory.getLogger(OrientdbPropertyConfig.class);

    @Value("${my.orientdb.location}")
    private String location = "remote:10.129.27.104/";
    @Value("${my.orientdb.account}")
    private String account = "root";
    @Value("${my.orientdb.password}")
    private String password = "wyc";  //NOSONAR
    @Value("${my.orientdb.dbType}")
    private String dbType = "graph";
    @Value("${my.orientdb.storeMode}")
    private String storeMode = "plocal";
    @Value("${my.orientdb.defaultPrefix}")
    private String defaultPrefix = "";
    @Value("${my.orientdb.discoverSpace}")
    private String discoverSpace = "test";
    @Value("${my.orientdb.connectionModel}")
    private String connectionModel;
    @Value("${my.orientdb.dataParallelCoefficient}")
    private String dataParallelCoefficient = "4";

    @Value("${my.temp-file.path:/root/temp/}")
    private String tempPath = "/root/temp/";
    @Value("${my.minio.bucket:pcop-cim}")
    private String bucket = "pcop-cim";
    @Value("${my.minio.url:http://10.129.57.108:9000/}")
    private String url = "http://10.129.57.108:9000/";
    @Value("${my.minio.user-name:pcop}")
    private String userName = "pcop";
    @Value("${my.minio.password:pcoppcop}")
    private String pwd = "pcoppcop";  //NOSONAR

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

    public static String TEMP_FILE_PATH = "TEMP_FILE_PATH";
    public static String TEMP_MINIO_BUCKET = "TEMP_MINIO_BUCKET";
    public static String TEMP_MINIO_URL = "TEMP_MINIO_URL";
    public static String TEMP_MINIO_USERNAME = "TEMP_MINIO_USERNAME";
    public static String TEMP_MINIO_PASSWORD = "TEMP_MINIO_PASSWORD";  //NOSONAR

    public static Map<String, String> parameterMap;

    @PostConstruct
    public void fieldInit() {
        ImportCimConstants.setDefauleSpaceName(discoverSpace);
        Map<String, String> map = new HashMap<>();
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

        map.put(TEMP_FILE_PATH, tempPath);
        map.put(TEMP_MINIO_BUCKET, bucket);
        map.put(TEMP_MINIO_URL, url);
        map.put(TEMP_MINIO_USERNAME, userName);
        map.put(TEMP_MINIO_PASSWORD, pwd);

        for (Map.Entry<String, String> entity : map.entrySet()) {
            log.info("OrientDB configuration paramaters: key={}, value={}", entity.getKey(), entity.getValue());
        }
        parameterMap = map;//NOSONAR
        PropertyHandler.map = map;
    }

    public static String getPropertyValue(String key) {
        return parameterMap.get(key);
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
}
