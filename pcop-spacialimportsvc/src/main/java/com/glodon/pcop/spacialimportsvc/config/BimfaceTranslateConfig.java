package com.glodon.pcop.spacialimportsvc.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BimfaceTranslateConfig {
    private static final Logger log = LoggerFactory.getLogger(BimfaceTranslateConfig.class);

    @Value("${cim.bimface.translate-config}")
    private String translateConfig;

    // public static Map<String, String> configs = new HashMap<>();

    public static JSONObject jsonObject = new JSONObject();

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(translateConfig)) {
            log.info("===no bimface translate configuration");
        }
        String[] configArray = translateConfig.trim().split(";");

        for (String st : configArray) {
            if (StringUtils.isNotBlank(st) && st.contains(",")) {
                String[] kv = st.trim().split(",", 2);
                String key = kv[0];
                String value = kv[1];
                log.info("===bimface translate configuration: key=[{}], value=[{}]", key, value);

                if (value.toUpperCase().equals("TRUE")) {
                    jsonObject.put(key, true);
                } else if (value.toUpperCase().equals("FALSE")) {
                    jsonObject.put(key, false);
                } else {
                    jsonObject.put(key, value);
                }
            } else {
                log.error("===error bimface translate configuration: [{}]", st);
            }
        }
    }

    public String getTranslateConfig() {
        return translateConfig;
    }

    public void setTranslateConfig(String translateConfig) {
        this.translateConfig = translateConfig;
    }


}
