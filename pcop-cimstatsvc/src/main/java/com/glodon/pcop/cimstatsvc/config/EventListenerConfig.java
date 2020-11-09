package com.glodon.pcop.cimstatsvc.config;

import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheConfig;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
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
        } catch (Exception e) {
            log.warn("cache initiation failed", e);
        }
    }

}
