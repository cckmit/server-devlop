package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecordUpdateUtil {

    private static Logger log = LoggerFactory.getLogger(RecordUpdateUtil.class);

    public static void updateDataSetDef(CimDataSpace cds) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        String dataSetType = "CIM_BUILDIN_DATASET";
        InformationExplorer ie = cds.getInformationExplorer();

        ExploreParameters ep = new ExploreParameters();
        ep.setType(dataSetType);

        List<Fact> facts = ie.discoverFacts(ep);
        if (facts != null) {
            for (Fact fact : facts) {
                log.info("fact id is: {}", fact.getId());
                if (!fact.hasProperty("CIM_BUILDIN_DATASET_STRUCTURE_NAME")) {
                    fact.addProperty("CIM_BUILDIN_DATASET_STRUCTURE_NAME", "SINGLE");
                    // fact.setInitProperty("CIM_BUILDIN_DATASET_STRUCTURE_NAME", "SINGLE");
                    log.info("data set structure is add");
                }

                if (!fact.hasProperty("CIM_BUILDIN_DATASET_TYPE")) {
                    fact.addProperty("CIM_BUILDIN_DATASET_TYPE", "INSTANCE");
                    log.info("data set type is add");
                }
            }
        } else {
            log.info("no fact of {} is found", dataSetType);
        }
    }

    public static void main(String[] args) {
        String dataBase = "yuanjk";
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dataBase);
            updateDataSetDef(cds);
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
