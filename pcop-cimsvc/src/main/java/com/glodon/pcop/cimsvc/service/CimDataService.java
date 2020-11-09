package com.glodon.pcop.cimsvc.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * @author Jimmy.Liu(liuzm @ glodon.com), Jul/07/2018.
 */
@Service
public class CimDataService {

    @PostConstruct
    public void setUp() {
        System.out.println("=== bean initination callback method");
    }

    @PreDestroy
    public void preDestory() {
        System.out.println("=== bean destory callback method");
    }


}

