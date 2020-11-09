package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import org.ehcache.Cache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceCacheUtilTest {

    private static ServiceEhcacheConfig ehcacheConfig;
    private static ServiceCacheUtil cacheUtil;

    @Before
    public void setUp() throws Exception {
        ehcacheConfig = new ServiceEhcacheConfig();
        ehcacheConfig.setClusterEnable(true);
        ehcacheConfig.setTerracottaServer("terracotta://localhost:9410/clustered");
        ehcacheConfig.setDefaultResources("main");
        ehcacheConfig.setPrimaryResources("primary-server-resource");


        cacheUtil = new ServiceCacheUtil();
        cacheUtil.setEhcacheConfig(ehcacheConfig);
    }

    @Test
    public void cacheExpire() throws InterruptedException {
        String cacheName = "cache_expire_test_0920";
        Cache<String, String> cache = cacheUtil.createCacheWithExpiry(cacheName, String.class, String.class, 5L);
        System.out.println("cache is created");

        cache.put("k1", "v1");

        if (cache == null) {
            System.out.println("cache is null");
        } else {
            System.out.println("k1" + "=" + cache.get("k1"));
        }

        System.out.println("start to sleep");
        Thread.sleep(6 * 1000L);
        System.out.println("finish to sleep");
        if (cache == null) {
            System.out.println("cache is null");
        } else {
            System.out.println("k1" + "=" + cache.get("k1"));
        }
    }

    @After
    public void tearDown() throws Exception {
    }
}