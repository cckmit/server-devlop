package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheConfig;
import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import org.ehcache.Cache;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.time.Duration;

@Component
public class ServiceCacheUtil {
    private static Logger log = LoggerFactory.getLogger(ServiceCacheUtil.class);
    private static final String CLUSTER_RESOURCE_POOL = "CIM_BUILDIN_CLUSTER_RESOURCE_POOL_V1D0";

    private PersistentCacheManager cacheManager;
    private Boolean cacheEnable = true;
    private ServiceEhcacheConfig ehcacheConfig;
    private String CacheDir = "CacheDir";

    @Autowired
    private CimCacheConfig cimCacheConfig;

    public void cacheManagerInit() {
        if (!cacheEnable) {
            log.warn("cache is disabled"); //NOSONAR
            return;
        }
        if (cacheManager == null) {
            if (ehcacheConfig.getClusterEnable()) {
                CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder =
                        CacheManagerBuilder.newCacheManagerBuilder()
                                .with(ClusteringServiceConfigurationBuilder.cluster(
                                        URI.create(ehcacheConfig.getTerracottaServer()))
                                        .autoCreate()
                                        .defaultServerResource(ehcacheConfig.getDefaultResources())
                                        .resourcePool(CLUSTER_RESOURCE_POOL, cimCacheConfig.getResourcePoolSizeMb(),
                                                MemoryUnit.MB, ehcacheConfig.getPrimaryResources()));
                cacheManager = clusteredCacheManagerBuilder.build(true);
            } else {
                File cacheFile = new File(CacheDir, "cimsvc");
                log.info("cache file path: {}", cacheFile.getAbsolutePath());
                cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(cacheFile))
                        .build(true);
            }
        }
    }


    /**
     * 返回指定的cache，若该cache不存在，则创建对应的cache
     *
     * @param cacheName
     * @param _KClass
     * @param _VClass
     * @param <K>
     * @param <V>
     * @return
     */
    public synchronized <K, V> Cache<K, V> createCache(String cacheName, Class<K> _KClass,
                                                       Class<V> _VClass) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache;
        CacheConfiguration<K, V> cacheConfig;
        if (ehcacheConfig.getClusterEnable()) {
            cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                    ResourcePoolsBuilder
//                            .heap(2, MemoryUnit.MB)
                            .heap(30)
                            .with(ClusteredResourcePoolBuilder.clusteredShared(CLUSTER_RESOURCE_POOL)))
//                    .add(ClusteredStoreConfigurationBuilder.withConsistency(Consistency.STRONG))
                    .withExpiry(ExpiryPolicyBuilder.noExpiration())
                    .build();
        } else {
            cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                    ResourcePoolsBuilder
                            .heap(30)
                            .disk(cimCacheConfig.getResourcePoolSizeMb(), MemoryUnit.MB, true))
                    .withExpiry(ExpiryPolicyBuilder.noExpiration())
                    .build();
        }
        targetCache = cacheManager.createCache(cacheName, cacheConfig);
        log.info("New cache cache_name={} is created!", cacheName);
        return targetCache;
    }

    public synchronized <K, V> Cache<K, V> createCacheWithExpiry(String cacheName, Class<K> _KClass,
                                                                 Class<V> _VClass, Long timeToLiveExpirationSeconds) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache;
        CacheConfiguration<K, V> cacheConfig;
        if (ehcacheConfig.getClusterEnable()) {
            cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(2, MemoryUnit.MB)
                            .with(ClusteredResourcePoolBuilder.clusteredShared(CLUSTER_RESOURCE_POOL)))
                    .add(ClusteredStoreConfigurationBuilder.withConsistency(Consistency.STRONG))
                    .withExpiry(
                            ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(timeToLiveExpirationSeconds)))
                    .build();
        } else {
            cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(2, MemoryUnit.MB)
                            .disk(256, MemoryUnit.MB, true))
                    .withExpiry(
                            ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(timeToLiveExpirationSeconds)))
                    .build();
        }
        targetCache = cacheManager.createCache(cacheName, cacheConfig);
        log.info("New cache cache_name={} is created!", cacheName);
        return targetCache;
    }

    public synchronized <K, V> Cache<K, V> getOrCreateCache(String cacheName, Class<K> _KClass,
                                                            Class<V> _VClass) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache = cacheManager.getCache(cacheName, _KClass, _VClass);
        if (targetCache == null) {
            log.info("cache [{}] not exists, new cache is created", cacheName);
            targetCache = createCache(cacheName, _KClass, _VClass);
        }
        return targetCache;
    }

    public synchronized <K, V> Cache<K, V> getCache(String cacheName, Class<K> _KClass,
                                                    Class<V> _VClass) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache = cacheManager.getCache(cacheName, _KClass, _VClass);
        return targetCache;
    }

    public synchronized <K, V> Cache<K, V> getOrCreateCacheWithExpiry(String cacheName, Class<K> _KClass,
                                                                      Class<V> _VClass,
                                                                      Long timeToLiveExpirationSeconds) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache = cacheManager.getCache(cacheName, _KClass, _VClass);
        if (targetCache == null) {
            log.info("cache [{}] not exists, new cache is created", cacheName);
            targetCache = createCacheWithExpiry(cacheName, _KClass, _VClass, timeToLiveExpirationSeconds);
        }
        return targetCache;
    }

    public synchronized <K, V> Cache<K, V> destoryCache(String cacheName, Class<K> _KClass,
                                                        Class<V> _VClass) {
        if (!cacheEnable) {
            log.info("cache is disabled"); //NOSONAR
            return null;
        }
        if (cacheManager == null) {
            cacheManagerInit();
        }
        Cache<K, V> targetCache = cacheManager.getCache(cacheName, _KClass, _VClass);
        log.info("===cache [{}] is removed", cacheName);
        try {
            cacheManager.destroyCache(cacheName);
        } catch (CachePersistenceException e) {
            e.printStackTrace();
        }
        return targetCache;
    }

    public Boolean getCacheEnable() {
        return cacheEnable;
    }

    public void setCacheEnable(Boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

    public ServiceEhcacheConfig getEhcacheConfig() {
        return ehcacheConfig;
    }

    public void setEhcacheConfig(ServiceEhcacheConfig ehcacheConfig) {
        this.ehcacheConfig = ehcacheConfig;
    }

    public String getCacheDir() {
        return CacheDir;
    }

    public void setCacheDir(String cacheDir) {
        CacheDir = cacheDir;
    }
}
