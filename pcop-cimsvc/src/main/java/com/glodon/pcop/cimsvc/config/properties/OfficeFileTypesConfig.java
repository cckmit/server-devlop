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
public class OfficeFileTypesConfig {
    private static final Logger log = LoggerFactory.getLogger(OfficeFileTypesConfig.class);

    private List<String> officeFileTypes = new ArrayList<>();

    public List<String> getOfficeFileTypes() {
        return officeFileTypes;
    }

    @PostConstruct
    public void init() {
        for (String st : officeFileTypes) {
            CimConstants.OFFICE_FILE_TYPES.add(st);
            log.info("=== - {}", st);
        }
        log.info("===office file types size: {}", CimConstants.OFFICE_FILE_TYPES.size());
    }
}
