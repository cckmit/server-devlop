package com.glodon.pcop.cimsvc.util.mock;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShortestPathDataInitTest {

    private static ShortestPathDataInit shortestPathDataInit = new ShortestPathDataInit();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
    }

    @Test
    public void typeInit() {
    }

    @Test
    public void addVertexAndEdge() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            shortestPathDataInit.typeInit(cds);
            shortestPathDataInit.addVertexAndEdge(cds);
        } catch (CimDataEngineDataMartException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }
}