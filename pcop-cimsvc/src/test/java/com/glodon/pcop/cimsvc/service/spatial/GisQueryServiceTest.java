package com.glodon.pcop.cimsvc.service.spatial;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.exception.GisServerErrorException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.gis.GisSqlQueryInput;
import com.glodon.pcop.cimsvc.model.v2.gis.SpatialAnalysisQueryInput;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GisQueryServiceTest {

    private GisQueryService gisQueryService;

    @Before
    public void setUp() throws Exception {
        String queryUrl = "http://10.129.57.118:8002/query";
        gisQueryService = new GisQueryService();
        gisQueryService.setQueryUrl(queryUrl);

        gisQueryService.setInstancesService(new InstancesService());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void spatialQuery() throws GisServerErrorException {
        String input = "{\"action\":\"query\",\"data\":{\"query_object\":{\"entity\":{\"feature_class" +
                "\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"},\"join\":{\"0\":{\"type\":\"inner\"," +
                "\"query_object\":{\"entity\":{\"feature_class\":\"inputgeometry12345\",\"tenant_id\":\"virtual\"," +
                "\"feature_data\":{\"head\":{\"0\":{\"column\":{\"name\":\"geo\",\"type\":\"geometry\"}}}," +
                "\"data\":[{\"0\":\"POLYGON ((460079.2689 2864373.1387, 460168.1737 2864582.7829, 460239.0331 2864533" +
                ".1087, 460243.2271 2864518.0645, 460171.3319 2864355.3235, 460157.6363 2864349.0689, 460079.2689 " +
                "2864373.1387))\"}]}},\"condition\":{\"function\":{\"0\":{\"column\":{\"name\":\"boundingbox_offset" +
                "\",\"type\":\"geometry\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"}}}," +
                "\"1\":{\"column\":{\"name\":\"geo\",\"type\":\"geometry\"," +
                "\"entity\":{\"feature_class\":\"inputgeometry12345\",\"tenant_id\":\"virtual\"}}}," +
                "\"name\":\"st_intersects\"}}}},\"1\":{\"type\":\"inner\"," +
                "\"query_object\":{\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}," +
                "\"condition\":{\"function\":{\"0\":{\"column\":{\"name\":\"boundingbox_offset\"," +
                "\"type\":\"geometry\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"}}}," +
                "\"1\":{\"column\":{\"name\":\"boundingbox_offset\",\"type\":\"geometry\"," +
                "\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}}}," +
                "\"2\":{\"object\":{\"value\":100,\"type\":\"integer\"}},\"name\":\"st_dwithin\"}}}}}," +
                "\"group\":{\"0\":{\"column\":{\"name\":\"ghyd_zt_gray_id\"}}," +
                "\"1\":{\"column\":{\"name\":\"ghyd_zt_gray_copy_id\"}}}," +
                "\"output\":{\"0\":{\"column\":{\"name\":\"id\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\"," +
                "\"tenant_id\":\"1\"}},\"name\":\"ghyd_zt_gray_id\"},\"1\":{\"column\":{\"name\":\"id\"," +
                "\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}}," +
                "\"name\":\"ghyd_zt_gray_copy_id\"},\"type\":\"distinct\"}}}}";
        Long startDate = System.currentTimeMillis();
        System.out.println("query result: " + gisQueryService.spatialQuery(input));
        System.out.println("used time millionseconds: " + (System.currentTimeMillis() - startDate));
    }

    @Test
    public void compositeQueryGis() {
    }

    @Test
    public void objectInstanceQuery() {
    }

    @Test
    public void spatialQuery1() throws InputErrorException, GisServerErrorException {

        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        String gisQueryInput = "{\"action\":\"query\",\"data\":{\"query_object\":{\"entity\":{\"feature_class" +
                "\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"},\"join\":{\"0\":{\"type\":\"inner\"," +
                "\"query_object\":{\"entity\":{\"feature_class\":\"inputgeometry12345a\",\"tenant_id\":\"virtual\"," +
                "\"feature_data\":{\"head\":{\"0\":{\"column\":{\"name\":\"geo\",\"type\":\"geometry\"}}}," +
                "\"data\":[{\"0\":\"POLYGON ((460079.2689 2864373.1387, 460168.1737 2864582.7829, 460239.0331 2864533" +
                ".1087, 460243.2271 2864518.0645, 460171.3319 2864355.3235, 460157.6363 2864349.0689, 460079.2689 " +
                "2864373.1387))\"}]}},\"condition\":{\"function\":{\"0\":{\"column\":{\"name\":\"boundingbox_offset" +
                "\",\"type\":\"geometry\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"}}}," +
                "\"1\":{\"column\":{\"name\":\"geo\",\"type\":\"geometry\"," +
                "\"entity\":{\"feature_class\":\"inputgeometry12345a\",\"tenant_id\":\"virtual\"}}}," +
                "\"name\":\"st_intersects\"}}}},\"1\":{\"type\":\"inner\"," +
                "\"query_object\":{\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}," +
                "\"condition\":{\"function\":{\"0\":{\"column\":{\"name\":\"boundingbox_offset\"," +
                "\"type\":\"geometry\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\",\"tenant_id\":\"1\"}}}," +
                "\"1\":{\"column\":{\"name\":\"boundingbox_offset\",\"type\":\"geometry\"," +
                "\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}}}," +
                "\"2\":{\"object\":{\"value\":100,\"type\":\"integer\"}},\"name\":\"st_dwithin\"}}}}}," +
                "\"group\":{\"0\":{\"column\":{\"name\":\"ghyd_zt_gray_id\"}}," +
                "\"1\":{\"column\":{\"name\":\"ghyd_zt_gray_copy_id\"}}}," +
                "\"output\":{\"0\":{\"column\":{\"name\":\"id\",\"entity\":{\"feature_class\":\"ghyd_zt_gray\"," +
                "\"tenant_id\":\"1\"}},\"name\":\"ghyd_zt_gray_id\"},\"1\":{\"column\":{\"name\":\"id\"," +
                "\"entity\":{\"feature_class\":\"ghyd_zt_gray_copy\",\"tenant_id\":\"1\"}}," +
                "\"name\":\"ghyd_zt_gray_copy_id\"},\"type\":\"distinct\"}}}}";

        GisSqlQueryInput gisCond = new GisSqlQueryInput();
        gisCond.setQuerySql(gisQueryInput);

        SpatialAnalysisQueryInput queryConditions = new SpatialAnalysisQueryInput();
        queryConditions.setCimIdKey("ghyd_zt_gray_id");
//        queryConditions.setObjectTypeId("GHYD_ZT_gray");
        queryConditions.setSpatialCondition(gisCond);
        String tenantId = "1";

        System.out.println("query result: " + JSON.toJSONString(gisQueryService.compositeQueryGis(tenantId,
                queryConditions)));

    }
}