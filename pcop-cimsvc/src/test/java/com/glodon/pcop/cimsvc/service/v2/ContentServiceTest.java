package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ContentServiceTest {

    private static String cimDataSpace;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        cimDataSpace = "pcopcim";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listChildNode() {
    }

    @Test
    public void relationableToOutBean() {
    }

    @Test
    public void infoObjectToFileNode() {
    }

    @Test
    public void deleteContentNode() {
    }

    @Test
    public void updateNodeMetadata() {
    }

    @Test
    public void industryAndInstanceRelation() {
    }

    @Test
    public void getNodeMetadata() {
    }

    @Test
    public void getIndustryAndObjectMapping() {
    }

    @Test
    public void moveNodes() {
    }

    // @Test
    public void fileAndInstacnes() {
        String instanceRid = "#4520:0";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimDataSpace);

            Fact fact = cds.getFactById(instanceRid);
            List<Relation> relationList = fact.getAllSpecifiedRelations(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME, RelationDirection.TO);
            for (Relation relation : relationList) {
                System.out.println("relationRid=" + relation.getId());
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }
}