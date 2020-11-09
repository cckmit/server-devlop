package com.glodon.pcop.cimsvc.config.listener;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheConfig;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cimsvc.service.tree.DataPermissionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = {"classpath:CimCacheConfig.properties"})
public class EventListenerConfig {
    private static final Logger log = LoggerFactory.getLogger(EventListenerConfig.class);

    @Autowired
    private DataPermissionCache dataPermissionCache;

    @Value("${cim.ehcache.clear-cache}")
    private Boolean clearCache;

    @Bean
    @ConfigurationProperties(prefix = "cim.cache.config")
    public CimCacheConfig getCimCacheConfig() {
        return new CimCacheConfig();
    }

    @PostConstruct
    public void init() {
        System.out.println("===" + getCimCacheConfig() + "===");
    }

    @EventListener
    public void handleApplicationPreparedEvent(ApplicationPreparedEvent startedEvent) {
        log.info("===handle application prepared event");
        try {
            log.info("===[{}]", getCimCacheConfig());
            CimCacheManager.setCimCacheConfig(getCimCacheConfig());
            log.info("===clear cache: [{}]", clearCache);
            if (clearCache) {
                CimCacheManager.clearDefCaches(CimConstants.defauleSpaceName);
                dataPermissionCache.dataPermissionCacheInit();
            }
        } catch (Exception e) {
            log.warn("cache initiation failed", e);
        }
    }

    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent startedEvent) {
        log.info("===handle application started event");
    }

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent readyEvent) {
        log.info("===handle application ready event");
    }

}
