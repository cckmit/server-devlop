package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cimstatsvc.config.SysConfigurations;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.InfoObjectOperation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory.getCIMModelCore;


public class DataStructStatistic {

    public static void count() {
        String tenantId = SysConfigurations.getDefaultTenantId();
        CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace("pcopcim");
        try {
            countDataStruct(cimDataSpace, tenantId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cimDataSpace != null) {
                cimDataSpace.closeSpace();
            }
        }

    }



    private static void countDataStruct(CimDataSpace cimDataSpace, String tenantId) {
        CIMModelCore cimModelCore = getCIMModelCore(cimDataSpace.getSpaceName(), tenantId);
        cimModelCore.setCimDataSpace(cimDataSpace);
        Long unStructuredDataNumber = 0L;
        try {
            unStructuredDataNumber = cimModelCore.getInfoObjectDef(ObjectTypeIdConstant.BASE_FILE_METADATA_INFO).countInstanceNumber();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }

        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(BusinessLogicConstant.CIM_TENANT_DIMENSION_TYPE_NAME);
        FilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.CIM_TENANT_DIMENSION_FIELDNAME_TENANT_ID, tenantId);
        exploreParameters.setDefaultFilteringItem(filteringItem);

        List<Dimension> list = new ArrayList<>();


        try {
            list = cimDataSpace.getInformationExplorer().discoverDimensions(exploreParameters);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }
        Long structuredDataNumber = ((Relationable) list.get(0)).countRelations();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        String date = sdf.format(c.getTime()); //上月


        HashMap<String, Object> item = new HashMap<>();
        item.put("structuredDataNumber", structuredDataNumber);
        item.put("unStructuredDataNumber", unStructuredDataNumber);
        item.put("date", date);
        item.put("ID", tenantId + "|" + date);

        HashMap<String, Object> query = new HashMap<>();
        query.put("ID", tenantId + "|" + date);
        query.put("date", date);
        InfoObjectOperation infoObjectOperation = new InfoObjectOperation(cimModelCore.getInfoObjectDef(ObjectTypeIdConstant.DATA_STATISTICS));
        infoObjectOperation.updateOrInsertOneToCim(item, query);
    }

}
