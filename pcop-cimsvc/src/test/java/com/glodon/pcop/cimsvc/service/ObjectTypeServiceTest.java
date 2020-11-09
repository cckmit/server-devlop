package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimsvc.model.ObjectQueryOutput;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ObjectTypeServiceTest {

//    private ObjectTypeService objectTypeService;
//
//
//    @BeforeClass
//    public void initVariable() {
//        objectTypeService = new ObjectTypeService();
//    }

//    @Test
    public void queryAllObjectsByPageTest() {

        InstanceQueryInputBean queryInputBean = new InstanceQueryInputBean();
        queryInputBean.setPageSize(20);
        queryInputBean.setStartPage(1);
        queryInputBean.setEndPage(2);

        Map<String, String> conditions = new HashMap<>();
        conditions.put("infoObjectTypeDesc", "a");

        queryInputBean.setConditions(conditions);

        ObjectTypeService objectTypeService = new ObjectTypeService();
        ObjectQueryOutput queryOutput  = objectTypeService.queryAllObjectsByPage(queryInputBean);
        System.out.println(queryOutput.getObjects().size());

    }

    @Test
    public void queryAllObjectsByPage() {
        InstanceQueryInputBean queryInputBean = new InstanceQueryInputBean();
//        queryInputBean.

    }
}