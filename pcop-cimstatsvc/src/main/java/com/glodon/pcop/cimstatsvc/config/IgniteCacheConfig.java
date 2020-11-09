package com.glodon.pcop.cimstatsvc.config;

import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangd-a
 * @date 2020/7/8 15:12
 */
//@Configuration
//@EnableCaching
public class IgniteCacheConfig {
	@Bean
	public CacheManager cacheManager() {

		SpringCacheManager cacheManager = new SpringCacheManager();
		cacheManager.setConfigurationPath("engine-ignite.xml");
		return cacheManager;
	}
}
