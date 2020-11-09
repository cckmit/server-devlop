package com.glodon.pcop.cimstatsvc.dao;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoObjectOperation {
    private  InfoObjectDef infoObjectDef;
    public InfoObjectOperation(InfoObjectDef infoObjectDef){
        this.infoObjectDef = infoObjectDef;
    }

    public void updateOrInsertOneToCim(Map<String, Object> instanceMap,Map<String, Object> queryMap){

        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(infoObjectDef.getObjectTypeName());

        List<EqualFilteringItem> list = new ArrayList<>();
        for (String key : queryMap.keySet()) {
            list.add(new EqualFilteringItem(key, queryMap.get(key)));
        }
        for (int index = 0; index < list.size(); index++) {
            if (index == 0) {
                exploreParameters.setDefaultFilteringItem(list.get(index));
                continue;
            }
            exploreParameters.addFilteringItem(list.get(index), ExploreParameters.FilteringLogic.AND);
        }
        exploreParameters.setStartPage(0);
        exploreParameters.setPageSize(10000);

        InfoObjectRetrieveResult SS = infoObjectDef.getObjects(exploreParameters);
        InfoObjectValue infoObjectValue = new InfoObjectValue();
        HashMap<String,Object>  base = new HashMap<>();
        System.out.println(infoObjectDef.getObjectTypeName());
        base.put("objectTypeId",infoObjectDef.getObjectTypeName());
        if(instanceMap.get("ID") != null){
            base.put("ID",instanceMap.get("ID"));
        }
        infoObjectValue.setBaseDatasetPropertiesValue(base);
        infoObjectValue.setGeneralDatasetsPropertiesValue(instanceMap);

        if(SS.getInfoObjects() != null && SS.getInfoObjects().size()> 0){
            infoObjectValue.setObjectInstanceRID(SS.getInfoObjects().get(0).getObjectInstanceRID());
            try {
                infoObjectDef.updateObject(infoObjectValue);
            } catch (DataServiceUserException e) {
                e.printStackTrace();
            }
        }else{
            try {
                infoObjectDef.newObject(infoObjectValue,false);
            } catch (DataServiceUserException e) {
                e.printStackTrace();
            }
        }
    }



}
