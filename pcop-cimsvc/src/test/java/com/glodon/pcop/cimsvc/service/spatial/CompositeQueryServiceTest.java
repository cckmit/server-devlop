package com.glodon.pcop.cimsvc.service.spatial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cimsvc.PcopCimsvcApplication;
import com.glodon.pcop.cimsvc.exception.GisServerErrorException;
import com.glodon.pcop.cimsvc.model.v2.CompositeInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.GisSpatialQueryConditionInputBean;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

// @RunWith(SpringJUnit4ClassRunner.class)
// @SpringBootTest(classes = {PcopCimsvcApplication.class})
@Ignore
public class CompositeQueryServiceTest {
    private static CompositeQueryService compositeQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        compositeQueryService = new CompositeQueryService();
        compositeQueryService.setCircleBufferUrl("http://10.129.57.118:7201/glodon/3DGISServer/place/search");
        compositeQueryService.setPolygonBuffer("http://10.129.57.118:7201/glodon/3DGISServer/place/search");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void compositeQueryGis() throws IOException, GisServerErrorException {
        String condistionStr = "{\"general_conditions\":[{\"filter_type\":\"EqualFilteringItem\"," +
                "\"first_param\":\"G2\",\"property_name\":\"Code\"}],\"order_by\":[\"ID\"],\"sort\":\"ASC\"," +
                "\"spatial_condition\":{\"boundary\":\"{\\\"type\\\":\\\"Polygon\\\", \\\"Positions\\\":[539" +
                ".2571569267345, 679.813042217982,-0.05911603197455406,488.9789308994693, 518.546401930158,-0" +
                ".03986918739974499,537.5672337962931, 463.962264916976,-0.039556979201734066,640.3516316647326, 509" +
                ".22406388737727,-0.052501317113637924,640.3589067310711, 510.5926344260806,-0.0526118129491806,640" +
                ".3589067310711, 510.5926344260806,-0.0526118129491806,539.2571569267345, 679.813042217982,-0" +
                ".05911603197455406,539.2571569267345, 679.813042217982,-0.05911603197455406]}\"," +
                "\"output\":\"{\\\"featureClassName\\\":[\\\"PLAN_KG_GHYD\\\"], " +
                "\\\"fieldName\\\":[\\\"objectName\\\",\\\"showName\\\",\\\"bondingbox\\\", \\\"combineNodeName\\\", " +
                "\\\"combineInfo\\\"]}\",\"query_type_enum\":\"POLYGON\"}}";

        String tenantId = "2";
        String objectTypeId = "PLAN_KG_GHYD";

        CompositeInstancesQueryInput queryConditions;
        queryConditions = objectMapper.readValue(condistionStr, CompositeInstancesQueryInput.class);

        System.out.println("===general condition: " + queryConditions.getGeneralConditions());
        System.out.println("===spatial condition: " + queryConditions.getSpatialCondition());

        // SingleQueryOutput queryOutput = compositeQueryService.compositeQueryGis(tenantId, objectTypeId,
        //         queryConditions);

        // System.out.println("===query out count: " + queryOutput.getTotalCount());
        // System.out.println("===query out instance: " + queryOutput.getInstances());
    }

    @Test
    public void gisBufferQuery() throws GisServerErrorException {
        GisSpatialQueryConditionInputBean gisSpatialQuery = new GisSpatialQueryConditionInputBean();
        gisSpatialQuery.setQueryTypeEnum(GisSpatialQueryConditionInputBean.GisQueryTypeEnum.POLYGON);
        gisSpatialQuery.setBoundary("{\"type\":\"Polygon\", \"Positions\":[539.2571569267345, 679.813042217982,-0" +
                ".05911603197455406,488.9789308994693, 518.546401930158,-0.03986918739974499,537.5672337962931, 463" +
                ".962264916976,-0.039556979201734066,640.3516316647326, 509.22406388737727,-0.052501317113637924,640" +
                ".3589067310711, 510.5926344260806,-0.0526118129491806,640.3589067310711, 510.5926344260806,-0" +
                ".0526118129491806,539.2571569267345, 679.813042217982,-0.05911603197455406,539.2571569267345, 679" +
                ".813042217982,-0.05911603197455406]}");
        gisSpatialQuery.setOutput("{\"featureClassName\":[\"PLAN_KG_GHYD\"], \"fieldName\":[\"objectName\"," +
                "\"showName\",\"bondingbox\", \"combineNodeName\", \"combineInfo\"]}");

        System.out.println("query result: " + compositeQueryService.gisBufferQuery(gisSpatialQuery));

    }

}