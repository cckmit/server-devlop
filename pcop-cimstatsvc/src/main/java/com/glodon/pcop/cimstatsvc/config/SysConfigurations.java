package com.glodon.pcop.cimstatsvc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SysConfigurations {
    private static Logger log = LoggerFactory.getLogger(SysConfigurations.class);


    private static String defaultTenantId = "3";

    @Value("${sys.defaultTenantId}")
    public  void setDefaultTenantId(String defaultTenantId) {
        SysConfigurations.defaultTenantId = defaultTenantId;
        log.info("defaultTenantId = {}",defaultTenantId );
    }

    public static String getDefaultTenantId() {
        return defaultTenantId;
    }

}
