package com.glodon.pcop.cimsvc.service.spatial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.shp.ShpMappingImportBean;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ShpLoaderTest {

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createOClass() {
    }

    @Test
    public void contentInsert() {
    }

    @Test
    public void createObjectTypeAndDataSet() {
    }

    @Test
    public void shpMappingImport() {
        CimConstants.defauleSpaceName = "pcopcim";
        String tenantId = "3";

        ShpMappingImportBean importBean = new ShpMappingImportBean();

        importBean.setObjectTypeId("test_obj_id_01");
        importBean.setObjectTypeName("test_obj_name_01");
        importBean.setDataSetId("test_ds_id_01");
        importBean.setDataSetName("test_ds_name_01");

        Map<String, String> pros = new HashMap<>();
        pros.put("pro1", "pro1");
        pros.put("pro2", "pro2");

        importBean.setPropertyMapping(pros);

        System.out.println(new Gson().toJson(importBean));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        try {
            System.out.println(objectMapper.writeValueAsString(importBean));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            ShpLoader.createObjectTypeAndDataSet(tenantId, importBean, modelCore);
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }
}