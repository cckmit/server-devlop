package com.glodon.pcop.cim.common.util;

import java.util.HashMap;
import java.util.Map;

public class OrientdbConfigUtil {
    public static Map<String, String> getParameters() {
        Map<String, String> parmMap = new HashMap<>();

         String location = "remote:10.2.23.57/";
//        String location = "remote:localhost/";
        String account = "root";
        String pass_word = "wyc";
        String dbType = "graph";
        String storeMode = "plocal";
        String defaultPrefix = "";
        String discoverSpace = "test";
        String clusterEnable = "false";
        String clusterLocation = "terracotta://localhost:9410/clustered";
        String clusterDefaultResourceId = "main";
        String clusterShareResourceId = "primary-server-resource";
        String cacheEnable = "false";

        String DISCOVER_ENGINE_SERVICE_LOCATION = "DISCOVER_ENGINE_SERVICE_LOCATION";
        String DISCOVER_ENGINE_ADMIN_ACCOUNT = "DISCOVER_ENGINE_ADMIN_ACCOUNT";
        String DISCOVER_ENGINE_ADMIN_PWD = "DISCOVER_ENGINE_ADMIN_PWD";
        String DISCOVER_SPACE_DATABASE_TYPE = "DISCOVER_SPACE_DATABASE_TYPE";
        String DISCOVER_SPACE_STORAGE_MODE = "DISCOVER_SPACE_STORAGE_MODE";
        String DISCOVER_DEFAULT_PREFIX = "DEFAULT_PREFIX";
        String META_CONFIG_DISCOVERSPACE = "META_CONFIG_DISCOVERSPACE";
        String CLUSTER_RESOURCE_CACHE_ENABLE = "CLUSTER_RESOURCE_CACHE_ENABLE";
        String CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION = "CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION";
        String CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID = "CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID";
        String CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID = "CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID";
        String CACHE_ENABLE = "CACHE_ENABLE";


        parmMap.put(DISCOVER_ENGINE_SERVICE_LOCATION, location);
        parmMap.put(DISCOVER_ENGINE_ADMIN_ACCOUNT, account);
        parmMap.put(DISCOVER_ENGINE_ADMIN_PWD, pass_word);
        parmMap.put(DISCOVER_SPACE_DATABASE_TYPE, dbType);
        parmMap.put(DISCOVER_SPACE_STORAGE_MODE, storeMode);
        parmMap.put(DISCOVER_DEFAULT_PREFIX, defaultPrefix);
        parmMap.put(META_CONFIG_DISCOVERSPACE, discoverSpace);
        parmMap.put(CLUSTER_RESOURCE_CACHE_ENABLE, clusterEnable);
        parmMap.put(CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION, clusterLocation);
        parmMap.put(CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID, clusterDefaultResourceId);
        parmMap.put(CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID, clusterShareResourceId);
        parmMap.put(CACHE_ENABLE, cacheEnable);

        for (Map.Entry<String, String> entity : parmMap.entrySet()) {
            System.out.println(
                    "OrientDB configuration paramaters: key=" + entity.getKey() + ", value=" + entity.getValue());
        }

        return parmMap;
    }
}
