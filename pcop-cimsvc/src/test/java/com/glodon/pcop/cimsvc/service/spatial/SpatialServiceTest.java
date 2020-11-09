package com.glodon.pcop.cimsvc.service.spatial;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cimsvc.model.shp.ShpMappingImportBean;
import com.glodon.pcop.cimsvc.model.spatial.BCQueryInput;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class SpatialServiceTest {

    private static SpatialService spatialService;

    @Before
    public void setUp() throws Exception {
        spatialService = new SpatialService();
    }

    @Test
    public void shpLoader() {
    }

    @Test
    public void bufferContainAnalysis() {
    }

    @Test
    public void getQueryByScopeAndGeneralPropertiesSql() {
        String objectTypeId = "KG_DKKG_GHDK";
        Map<String, String> properties = new HashMap<>();
        properties.put("ssqy", "住宅混合共建用地");
        // properties.put("key2", "value2");

        List<CommonQueryConditionsBean> conditions = new ArrayList<>();
        CommonQueryConditionsBean cond = new CommonQueryConditionsBean();
        cond.setFilterLogical(ExploreParameters.FilteringLogic.AND);
        cond.setFilterType("EqualFilteringItem");
        cond.setPropertyName("ssqy");
        cond.setFirstParam("住宅混合共建用地");

        CommonQueryConditionsBean cond2 = new CommonQueryConditionsBean();
        cond2.setFilterLogical(ExploreParameters.FilteringLogic.AND);
        cond2.setFilterType("InValueFilteringItem");
        cond2.setPropertyName("ID");
        cond2.setListParam(Arrays.asList("KG_DKKG_GHDK_876", "KG_DKKG_GHDK_869", "KG_DKKG_GHDK_70", "KG_DKKG_GHDK_296"
        ));

        conditions.add(cond);
        conditions.add(cond2);

        String wktArea = "Polygon ((119.9719999999999942 2.38609999999999989, 119.9719999999999942 2" +
                ".38450000000000006, 119.97350000000000136 2.38450000000000006, 119.97350000000000136 2" +
                ".38609999999999989, 119.9719999999999942 2.38609999999999989))";
        String statement = spatialService.getQueryByScopeAndGeneralPropertiesSql(objectTypeId, conditions, wktArea);
        System.out.println(statement);
    }

    @Test
    public void getQueryByBufferSql() {
        BCQueryInput.BufferInput bufferInput = new BCQueryInput.BufferInput();
        bufferInput.setObjectTypeId("target_object_type");
        bufferInput.setDistance(115);

        String wktArea = "MultiPolygon (((119.98199672081882738 2.41148073400850693, 119.98199671558811019 2" +
                ".41187864948349029, 119.98199671517201637 2.41191030185071309, 119.98212708389283421 2" +
                ".4120414348140149, 119.9821585526208878 2.41204143522918857, 119.98274967775166999 2" +
                ".41204144289208422, 119.98274968481420899 2.41148074381209732, 119.98199672081882738 2" +
                ".41148073400850693)))";
        String statement = spatialService.getQueryByBufferSql(wktArea, bufferInput);
        System.out.println(statement);

    }

    @Test
    public void StringTest() {
        List<String> stringList = new ArrayList<>();
        stringList.add("one");
        stringList.add("two");
        stringList.add("three");

        System.out.println(StringUtils.join(stringList, ','));
        System.out.println(StringUtils.join(stringList, null));

    }


    @Test
    public void circleBuffer() {
        String centerPoint = "POINT (12.4684635 41.8914114)";
        Double radius = 500D;
        String objectTypeId = "yuanjk_shp_test_aaaaa";

        System.out.println(spatialService.getCircleBufferSql(centerPoint, radius, objectTypeId));
    }


    @Test
    public void shpMappingImportDeserialize() {
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

    }

    @Test
    public void udpateShpMapping() throws IOException {
        // String mappingInfo = "{\"objectTypeId\":\"ZX_ZDHZXM_POINT\",\"objectTypeName\":\"ZX_ZDHZXM_POINT\"," +
        //         "\"dataSetId\":\"ZX_ZDHZXM_POINT_dataset0716\",\"dataSetName\":\"ZX_ZDHZXM_POINT_dataset0716\"}";

        String mappingInfo = "{\"objectTypeId\":\"ZX_ZDHZXM_POINT\",\"objectTypeName\":\"ZX_ZDHZXM_POINT\"}";

        String shpFile = "G:\\tmp\\ZX_ZDHZXM_POINT\\ZX_ZDHZXM_POINT.shp";

        System.out.println("result: " + JSON.toJSONString(spatialService.updateMappingInput(shpFile, mappingInfo)));

    }

}