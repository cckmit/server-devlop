package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.UIViewFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.UIViewVo;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class UIViewServiceTest {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // @Before
    public void init() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
        // CimConstants.defauleSpaceName = "test";
//        CacheUtil.cacheManagerInit();
    }

    // @Test
    public void getUIViewById() throws IOException {
        String viewId = "v001";
        UIViewVo viewVo = UIViewFeatures.getUIViewById(CimConstants.defauleSpaceName, viewId);
        // System.out.println(gson.toJson(viewVo));

        JsonNode jsonNode = objectMapper.readTree(viewVo.getViewData());

        System.out.println(jsonNode);
        System.out.println(jsonNode.get("properties"));
        List<PropertyBean> propertyBeans = objectMapper.readValue(jsonNode.get("properties").traverse(), new TypeReference<List<PropertyBean>>() {
        });

        if (propertyBeans != null) {
            System.out.println("propertyBeans size is: " + propertyBeans.size());
            for (PropertyBean bean : propertyBeans) {
                System.out.println("id=" + bean.getId() + ", name=" + bean.getName());
            }
        } else {
            System.out.println("propertyBeans is null");
        }

    }

    // @Test
    public void queryUIViewByObjectType() throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException, JsonProcessingException {
        String objectTypeId = "Binhai_Buildings";
        System.out.println(objectMapper.writeValueAsString(UIViewService.queryUIViewByObjectType(objectTypeId)));
    }


}