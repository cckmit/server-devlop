package com.glodon.pcop.cimsvc.config.properties;

import com.glodon.pcop.cimsvc.service.v2.FileDataImportService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class GisConfigurations {

    private static Logger log = LoggerFactory.getLogger(GisConfigurations.class);

    @Value("${gis-host.base-url}")
    private String gisBaseUrl;

    @Value("${gis-host.file-struct}")
    private String fileStruct;

    @Value("${gis-host.file-record}")
    private String fileRecord;

    @PostConstruct
    public void init() {
        if (StringUtils.isNotBlank(fileStruct)) {
            FileDataImportService.setGetStructUrl(fileStruct);
            log.info("gis get file struct url: {}", FileDataImportService.getStructUrl);
        } else {
            log.error("gis get file struct url is empty");
        }

        if (StringUtils.isNotBlank(fileRecord)) {
            FileDataImportService.setGetRecordUrl(fileRecord);
            log.info("gis get file record url: {}", FileDataImportService.getRecordUrl);
        } else {
            log.error("gis get file record url is empty");
        }
    }

}
