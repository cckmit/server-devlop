package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


public class DatasetTest {


    public static void main(String[] args) {
        System.out.println("start");
        CIMModelCore targetCIMModelCore =
                ModelAPIComponentFactory.getCIMModelCore("pcopcim","1");
        InfoObjectDef infoObjectDef = targetCIMModelCore.getInfoObjectDef("quxian");
        List<DatasetDef> datasetDefs = infoObjectDef.getDatasetDefs();
        for (DatasetDef datasetDef : datasetDefs) {
            System.out.println("dataset: "+ datasetDef.getDatasetName());
        }

    }





}
