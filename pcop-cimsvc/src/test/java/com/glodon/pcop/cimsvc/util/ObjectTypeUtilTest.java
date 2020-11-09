package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ObjectTypeUtilTest {

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void queryFactById() {
    }

    @Test
    public void clearObjects() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            ExploreParameters exploreParameters = new ExploreParameters();
            InformationExplorer ie = cds.getInformationExplorer();
            exploreParameters.setResultNumber(10 * 10000);
            exploreParameters.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);
            List<Fact> infoObjectDefStatusFactList = ie.discoverFacts(exploreParameters);
            for (Fact currentStatusFact : infoObjectDefStatusFactList) {
                String objectTypeName = currentStatusFact.getProperty(
                        BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_PROPERTY_NAME_infoObjectTypeName).getPropertyValue().toString();
                if (!cds.hasInheritFactType(objectTypeName)) {
                    System.out.println("inherit fact type not exists [" + objectTypeName + "]");
                }
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }


    }

}