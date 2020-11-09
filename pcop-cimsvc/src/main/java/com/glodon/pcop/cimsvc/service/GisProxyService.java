package com.glodon.pcop.cimsvc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GisProxyService {
    private static final Logger log = LoggerFactory.getLogger(GisProxyService.class);

    @Autowired
    private RestTemplate restTemplate;


    public Object poxyQuery(HttpMethodTypeEnum methodType, String methodName, String content) {


        return null;
    }


    public enum HttpMethodTypeEnum {
        GET, POST
    }

}
