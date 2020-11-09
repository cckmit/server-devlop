package com.glodon.pcop.cimsvc.config.properties;

import com.glodon.pcop.cim.common.util.CimConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cim")
public class BimFileTypesConfig {
    private static final Logger log = LoggerFactory.getLogger(BimFileTypesConfig.class);

    private List<String> bimFileTypes = new ArrayList<>();

    public List<String> getBimFileTypes() {
        return bimFileTypes;
    }

    @PostConstruct
    public void init() {
        for (String st : bimFileTypes) {
            CimConstants.BIM_FILE_TYPES.add(st);
            log.info("=== - {}", st);
        }
        log.info("===bim file types size: {}", CimConstants.BIM_FILE_TYPES.size());
    }
}
