package com.glodon.pcop.cimsvc.service.graph;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.model.vo.InstanceRelationsVO;
import com.glodon.pcop.cimsvc.service.dataGraph.DataGraphService;
import org.junit.Before;
import org.junit.Test;

public class GraphViewServiceTest {

    private static DataGraphService service = new DataGraphService();
    private static CimDataSpace cds = null;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "gdc";
    }



    @Test
    public void instanceView(){
        String instanceRID = "#151:2";


        InstanceRelationsVO instanceRelationsVO = service.loadInstanceRelationData(instanceRID);


        String otherInstanceRID ="#153:1";

        InstanceRelationsVO instanceRelationsVO1 = service.loadInstanceRelationData(otherInstanceRID);


        System.out.println(instanceRelationsVO
        );
    }


}