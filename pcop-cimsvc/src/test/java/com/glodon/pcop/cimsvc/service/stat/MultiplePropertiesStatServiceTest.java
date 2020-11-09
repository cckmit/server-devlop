package com.glodon.pcop.cimsvc.service.stat;

import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatInputBean;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatPropertyFilterBean;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MultiplePropertiesStatServiceTest {

    private static MultiplePropertiesStatService statService = new MultiplePropertiesStatService();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void multiplePropertyStat() {
    }

    @Test
    public void sqlBuilder() {

        String tennatId = "1";
        MultiplePropertiesStatInputBean statInputBean = new MultiplePropertiesStatInputBean();
        statInputBean.setObjectTypeId("projectV1");

        List<MultiplePropertiesStatPropertyFilterBean> properties = new ArrayList<>();
        MultiplePropertiesStatPropertyFilterBean filterBean1 = new MultiplePropertiesStatPropertyFilterBean();
        filterBean1.setProperty("bimfaceIdType");
        filterBean1.setPropertyValues(Arrays.asList("DATABAG", "INTEGRATEDID"));
        properties.add(filterBean1);

        MultiplePropertiesStatPropertyFilterBean filterBean2 = new MultiplePropertiesStatPropertyFilterBean();
        filterBean2.setProperty("projectType");
        filterBean2.setPropertyValues(Arrays.asList("001"));
        properties.add(filterBean2);

        statInputBean.setProperties(properties);

        System.out.println("sql: " + statService.sqlBuilder(tennatId, statInputBean));

    }
}