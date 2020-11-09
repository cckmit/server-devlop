package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionServiceTest {

    private static String dbName = "test";

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
    }

    @Test
    public void takeAction() {
    }

    @Test
    public void getActions() {
    }


    // @Test
    public void addAction() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);

            Fact fact = CimDataEngineComponentFactory.createFact(BusinessLogicConstant.ACTION_INFO_STATUS_FACT_TYPE_NAME);
            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_ACTION_NAME, "test_action_name_a");
            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_ACTION_DESC, "test_action_desc_a");
            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_ACTION_EXECUTION_CLASS, "com.glodon.pcop.cim.engine.dataServiceModelAPI.util.action.xxx");
            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_INFOOBJECT_TYPE, "cccc");
            cds.addFact(fact);

            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_ACTION_NAME, "test_action_name_a");
            fact.setInitProperty(BusinessLogicConstant.ACTION_INFO_STATUS_FIELDNAME_ACTION_DESC, "test_action_desc_a");
            cds.addFact(fact);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }
}