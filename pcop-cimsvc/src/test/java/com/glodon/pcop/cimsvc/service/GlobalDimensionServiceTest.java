package com.glodon.pcop.cimsvc.service;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.model.DatasetBean;
import com.glodon.pcop.cimsvc.model.PropertyTypeVOBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeInputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeOutputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeWithDatasetOutputBean;
import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class GlobalDimensionServiceTest {
    private static final Logger log = LoggerFactory.getLogger(GlobalDimensionServiceTest.class);

    private static GlobalDimensionService service = new GlobalDimensionService();

    private static final String DIMENSION_NAME = "test_dimension_name_20191231";
    private static final String DIMENSION_DATASET_NAME = "test_dimension_20191231_dataset_name";
    private static final String TENANT_ID = "1";

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addConfigurationDimension() {
        DimensionTypeInputBean inputBean = new DimensionTypeInputBean();
        inputBean.setDimensionTypeName(DIMENSION_NAME);
        inputBean.setDimensionTypeDesc(DIMENSION_NAME);

        DatasetBean datasetBean = new DatasetBean();
        datasetBean.setDatasetName(DIMENSION_DATASET_NAME);
        datasetBean.setDatasetDesc(DIMENSION_DATASET_NAME);
        datasetBean.setDatasetClassify("通用属性集");

        List<PropertyTypeVOBean> voBeanList = new ArrayList<>();
        PropertyTypeVOBean voBean1 = new PropertyTypeVOBean();
        voBean1.setPropertyTypeName("k1");
        voBean1.setPropertyTypeDesc("k1");
        voBean1.setPropertyFieldDataClassify("STRING");
        voBeanList.add(voBean1);

        PropertyTypeVOBean voBean2 = new PropertyTypeVOBean();
        voBean2.setPropertyTypeName("k2");
        voBean2.setPropertyTypeDesc("k2");
        voBean2.setPropertyFieldDataClassify("INT");
        voBeanList.add(voBean2);

        datasetBean.setLinkedPropertyTypes(voBeanList);

        inputBean.setLinkedDataset(datasetBean);


        boolean addResult = service.addConfigurationDimension(TENANT_ID, inputBean);
        log.info("dimension type add result: [{}]", addResult);
    }

    @Test
    public void getGlobalDimensionItems() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
//        PropertyHandler.map.put(PropertyHandler.CACHE_ENABLE, "false");
        CimConstants.defauleSpaceName = "pcopcim";
        String dimensionType = "EquipmentAccidentPlanDict";
        List<Map<String, Object>> rs = service.getGlobalDimensionItems(dimensionType, "@rid", ExploreParameters.SortingLogic.ASC);
        System.out.println("===" + JSON.toJSONString(rs));
    }

    @Test
    public void addGlobalDimensionItem() {
        Map<String, Object> items = new HashMap<>();
        items.put("k1", "v12");
        items.put("k2", 22);

        boolean addResult = service.addGlobalDimensionItem(DIMENSION_NAME, items);
        log.info("add dimension item result: [{}]", addResult);
    }

    @Test
    public void listGlobalDimensions() {
        List<DimensionTypeOutputBean> typeOutputBeanList = service.listGlobalDimensions(null, 0, 20);
        log.info("result size: [{}]", typeOutputBeanList.size());
        for (DimensionTypeOutputBean outputBean : typeOutputBeanList) {
            log.info("dimension name: [{}]", outputBean.getDimensionTypeName());
        }
    }

    @Test
    public void getDimensionType() {
        DimensionTypeWithDatasetOutputBean typeWithDatasetOutputBean =
                service.getDimensionType(DIMENSION_NAME);

        log.info("dimension type with data set: [{}]", JSON.toJSON(typeWithDatasetOutputBean));
    }

    @Test
    public void removeDimensionItem() {
        List<Map<String, Object>> items = service.getGlobalDimensionItems(DIMENSION_NAME, "@rid", ExploreParameters.SortingLogic.ASC);
        if (CollectionUtils.isNotEmpty(items)) {
            Map<String, Object> item = items.get(0);
            log.info("remove dimension item: [{}]", item);
            service.removeDimensionItem(DIMENSION_NAME, item.get("INFO_OBJECT_ID").toString());
        } else {
            log.error("dimension item is empty");
        }
    }

    @Test
    public void removeDimensionType() {
        boolean removeResult = service.removeDimensionType(DIMENSION_NAME);
        log.info("dimension type remove result: [{}]", removeResult);
    }
}