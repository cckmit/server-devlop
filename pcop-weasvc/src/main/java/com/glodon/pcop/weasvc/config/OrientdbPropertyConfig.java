package com.glodon.pcop.weasvc.config;

import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.weasvc.dao.BaseDao;

import com.glodon.pcop.weasvc.dao.BaseDao;
import com.glodon.pcop.weasvc.dao.BaseExDao;
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
//    @Value("${my.orientdb.clusterEnable}")
//    private String clusterEnable = "false";
//    @Value("${my.orientdb.clusterLocation}")
//    private String clusterLocation;
//    @Value("${my.orientdb.clusterDefaultResourceId}")
//    private String clusterDefaultResourceId = "main";
//    @Value("${my.orientdb.clusterShareResourceId}")
//    private String clusterShareResourceId = "primary-server-resource";

    public static String DISCOVER_ENGINE_SERVICE_LOCATION = "DISCOVER_ENGINE_SERVICE_LOCATION";
    public static String DISCOVER_ENGINE_ADMIN_ACCOUNT = "DISCOVER_ENGINE_ADMIN_ACCOUNT";
    public static String DISCOVER_ENGINE_ADMIN_PWD = "DISCOVER_ENGINE_ADMIN_PWD";  //NOSONAR
    public static String DISCOVER_SPACE_DATABASE_TYPE = "DISCOVER_SPACE_DATABASE_TYPE";
    public static String DISCOVER_SPACE_STORAGE_MODE = "DISCOVER_SPACE_STORAGE_MODE";
    public static String DISCOVER_DEFAULT_PREFIX = "DEFAULT_PREFIX";
    public static String META_CONFIG_DISCOVERSPACE = "META_CONFIG_DISCOVERSPACE";



    @SuppressWarnings("Duplicates")
    @PostConstruct
    public void fieldInit() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DISCOVER_ENGINE_SERVICE_LOCATION, location);
        map.put(DISCOVER_ENGINE_ADMIN_ACCOUNT, account);
        map.put(DISCOVER_ENGINE_ADMIN_PWD, password);
        map.put(DISCOVER_SPACE_DATABASE_TYPE, dbType);
        map.put(DISCOVER_SPACE_STORAGE_MODE, storeMode);
        map.put(DISCOVER_DEFAULT_PREFIX, defaultPrefix);
        map.put(META_CONFIG_DISCOVERSPACE, discoverSpace);


//        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_ENABLE, clusterEnable);
//        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION, clusterLocation);
//        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID, clusterDefaultResourceId);
//        map.put(PropertyHandler.CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID, clusterShareResourceId);
        for (Map.Entry<String, String> entity : map.entrySet()) {
            log.info("OrientDB configuration paramaters: key={}, value={}", entity.getKey(), entity.getValue());
        }
        PropertyHandler.map = map;
        BaseDao.dbName = discoverSpace;
        BaseExDao.dbName = discoverSpace;
    }
}
