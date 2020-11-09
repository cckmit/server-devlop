package com.glodon.pcop.cimsvc.service.graph;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.graph.TubulationAnalysisOutputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.path.OrientDBShortestPath;
import com.glodon.pcop.cimsvc.model.input.ConnectedInputBean;
import com.glodon.pcop.cimsvc.model.input.InstanceBaseBean;
import com.glodon.pcop.cimsvc.model.output.ConnectedOutputBean;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphAnalysisServiceTest {

    private static GraphAnalysisService service = new GraphAnalysisService();
    private static CimDataSpace cds = null;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
    }

    @Test
    public void instanceConnected() {
        InstanceBaseBean sourceBean = new InstanceBaseBean();
        sourceBean.setId("ID-1");
        sourceBean.setObjectTypeId("short_path_vertex_test");

        InstanceBaseBean destinationBean01 = new InstanceBaseBean();
        destinationBean01.setId("ID-0");
        destinationBean01.setObjectTypeId("short_path_vertex_test");

        InstanceBaseBean destinationBean02 = new InstanceBaseBean();
        destinationBean02.setId("ID-10");
        destinationBean02.setObjectTypeId("short_path_vertex_test");

        List<InstanceBaseBean> destinationBeanList = new ArrayList<>();
        destinationBeanList.add(destinationBean01);
        destinationBeanList.add(destinationBean02);

        ConnectedInputBean inputBean = new ConnectedInputBean();
        OrientDBShortestPath.SearchDirectionEnum directionEnum = OrientDBShortestPath.SearchDirectionEnum.BOTH;
        String relationType = "GLD_RELATION_short_path_edge_test";

        inputBean.setDirectionEnum(directionEnum);
        inputBean.setRelationType(relationType);
        inputBean.setSourceInstance(sourceBean);
        inputBean.setDestinationInstances(destinationBeanList);

        ConnectedOutputBean outputBean = service.instanceConnected(inputBean);

        System.out.println("connected output: " + outputBean);
    }

    @Test
    public void shortestPathVertexById() {
        String fromRid = "#7335:1";
        OrientDBShortestPath.SearchDirectionEnum directionEnum = OrientDBShortestPath.SearchDirectionEnum.BOTH;
        // String relationType = "GLD_RELATION_short_path_edge_test";
        String relationType = null;

        InstanceBaseBean baseBean = new InstanceBaseBean();
        baseBean.setId("ID-10");
        baseBean.setObjectTypeId("short_path_vertex_test");

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            List<OrientVertex> vertexList = service.shortestPathVertexById(cds, fromRid, baseBean, directionEnum,
                    relationType);
            System.out.println("vertex size: " + vertexList.size());
            for (OrientVertex vertex : vertexList) {
                System.out.println("V: " + vertex.getId());
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void travelseTest() {

//        String sql = "TRAVERSE both(\"GLD_RELATION_COMMON_SpaceAdjacent\") from  #7416:28 MAXDEPTH 100  STRATEGY " +
//                "BREADTH_FIRST";
        String sql = "select $path as path from ( TRAVERSE both(\"GLD_RELATION_COMMON_SpaceAdjacent\") from  #7416:28 MAXDEPTH 100  STRATEGY BREADTH_FIRST)";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            OrientGraph graph = ((OrientDBCimDataSpaceImpl) cds).getGraph();

//            Object data = graph.command(new OCommandSQL(sql)).execute();
//            System.out.println("data class type: " + data.getClass());
            Iterable<OrientVertex> data = (Iterable<OrientVertex>) graph.command(new OCommandSQL(sql)).execute();
            for (OrientVertex vl : data) {
//                System.out.println("data class type: " + JSON.toJSONString(vl.getProperty("path")));
                System.out.println("data class type: " + JSON.toJSONString(vl.getProperties()));
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }


    }


    @Test
    public void extractRid() {
        String REGEX = "#\\d+:\\d+";
        Pattern p = Pattern.compile(REGEX);

        String st = "(#7416:28)";
//        String st = "(#7416:28).both[0](#7346:413).both[2](#9162:61).both[1](#7346:453).both[0](#7409:30)";

//        String[] tokens = st.trim().split(".both");
        String[] tokens = st.trim().split("\\[\\d+\\]");

        for (String t : tokens) {
            Matcher m = p.matcher(t); // 获取 matcher 对象
            System.out.println("input: " + t);
            System.out.println("is match: " + m.find());
            System.out.println("match string:" + m.group());
        }


    }


    @After
    public void tearDown() throws Exception {
        if (cds != null) {
            cds.closeSpace();
        }

    }

    @Test
    public void testInstanceConnected() {
    }

    @Test
    public void testShortestPathVertexById() {
    }

    @Test
    public void tubulationAnalysis() {
    }

    @Test
    public void buildTraverseSql() {

        System.out.println(
                GraphAnalysisService.buildTraverseSql(RelationDirection.TWO_WAY, "COMMON_SpaceAdjacent", "#7416:28"));

    }

    @Test
    public void getTraversePathWithRids() {

        String sql = GraphAnalysisService.buildTraverseSql(RelationDirection.TWO_WAY, "COMMON_SpaceAdjacent",
                "#7416:28");

        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

        OrientGraph graph = ((OrientDBCimDataSpaceImpl) cds).getGraph();


        List<List<String>> pathsWithRids = GraphAnalysisService.getTraversePathWithRids(graph, sql);

        System.out.println("paths with rids: " + JSON.toJSONString(pathsWithRids));


    }

    @Test
    public void filterPathRidsByObjectTypes() {
        String sql = GraphAnalysisService.buildTraverseSql(RelationDirection.TWO_WAY, "COMMON_SpaceAdjacent",
                "#7345:369");

        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

        OrientGraph graph = ((OrientDBCimDataSpaceImpl) cds).getGraph();


        List<List<String>> pathsWithRids = GraphAnalysisService.getTraversePathWithRids(graph, sql);

        System.out.println("paths with rids: " + JSON.toJSONString(pathsWithRids));

        Set<String> outputObjectTypeIds = new HashSet<>();
        outputObjectTypeIds.add("UPNetwork_JS_FM_Point");
        outputObjectTypeIds.add("UPNetwork_JS_JSFMJ_Point");

        TubulationAnalysisOutputBean outputBean = GraphAnalysisService.filterPathRidsByObjectTypes(cds, pathsWithRids,
                outputObjectTypeIds);

        System.out.println("output bean: " + JSON.toJSONString(outputBean));
    }

}