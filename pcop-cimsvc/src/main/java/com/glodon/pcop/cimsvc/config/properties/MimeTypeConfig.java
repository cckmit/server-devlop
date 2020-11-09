package com.glodon.pcop.cimsvc.config.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mime")
@PropertySource(value = {"classpath:MimeType.properties"})
@Component
public class MimeTypeConfig {
    private static final Logger log = LoggerFactory.getLogger(MimeTypeConfig.class);

    private List<String> types = new ArrayList<>();

    private MimeMappings mimeMappings = new MimeMappings();

    @PostConstruct
    public void init() {
        for (String mm : types) {
            log.info("===MIME type [{}]", mm);
            String[] mmSplit = mm.split(",", 2);
            mimeMappings.add(mmSplit[0].trim(), mmSplit[1].trim());
        }
    }

    public String getMimeType(String extension) {
        return mimeMappings.get(extension);
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
