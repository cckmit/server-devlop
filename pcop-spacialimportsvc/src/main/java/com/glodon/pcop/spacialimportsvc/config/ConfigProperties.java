package com.glodon.pcop.spacialimportsvc.config;

import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ConfigProperties {
    @Value("${my.shp-parser}")
    private String shpParser;

    public static String shpParserVersion;

    @PostConstruct
    public void fieldInit() {
        System.out.println("default data space name: ImportCimConstants.defauleSpaceName=" + ImportCimConstants.defauleSpaceName);
        setShpParserVersion(shpParser);
        System.out.println("shp parser version is " + shpParserVersion);
    }

    public static void setShpParserVersion(String shpParserVersion) {
        ConfigProperties.shpParserVersion = shpParserVersion;
    }
}
