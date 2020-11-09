package com.glodon.pcop.cimstatsvc.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTransferVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao extends  DbExecute {
    protected static String IndexName = "INFO_OBJECT_ID";

    protected static Map object2Map(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        Map map = new HashMap();
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //json字符串转为Map对象
        try {
            map = mapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void saveToCim(String kindName, List<Map<String, Object>> listMaps) {
        List<Map<String, InfoObjectTransferVO>> dataContainerList = new ArrayList<>();
        for (Map<String, Object> map : listMaps) {
            Map<String, InfoObjectTransferVO> dataMap = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    dataMap.put(key, new InfoObjectTransferVO(map.get(key)));
                }
            }
            dataContainerList.add(dataMap);
        }



        InfoObjectFeatures.loadInfoObjectData(dbName, kindName, null, dataContainerList);
        dataContainerList.clear();
    }

    public static List<Map<String, Object>> QueryToCim(String kindName, Map<String, Object> map) {
        ExploreParameters ep = new ExploreParameters();
        List<EqualFilteringItem> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(new EqualFilteringItem(key, map.get(key)));
        }
        for (int index = 0; index < list.size(); index++) {
            if (index == 0) {
                ep.setDefaultFilteringItem(list.get(index));
                continue;
            }
            ep.addFilteringItem(list.get(index), ExploreParameters.FilteringLogic.AND);
        }
        ep.setStartPage(0);
        ep.setPageSize(100);
        return InfoObjectFeatures.queryInfoObjectData(dbName, kindName, ep);
    }

    public static boolean removeToCim(String kindName, String objectId) {
        return InfoObjectFeatures.removeInfoObjectInstance(dbName, kindName, objectId);
    }

    public static boolean removeToCim(String kindName, Map<String, Object> instanceData) {
        return InfoObjectFeatures.removeInfoObjectInstance(dbName, kindName, (String) instanceData.get(IndexName));
    }

    public static boolean updateToCim(String kindName, String objectId, Map<String, Object> instanceData) {
        Map<String, Object> data = new HashMap<>();
        for (String key : instanceData.keySet()) {
            if(instanceData.get(key) !=null ){
                data.put(key,instanceData.get(key));
            }
        }
        return InfoObjectFeatures.updateInfoObjectInstanceData(dbName, kindName, objectId, data);
    }

    public static boolean updateToCim(String kindName, Map<String, Object> instanceData) {
        if (((String) instanceData.get(IndexName)).equals("")) {
            return false;
        }
        return InfoObjectFeatures.updateInfoObjectInstanceData(dbName, kindName, ((String) instanceData.get(IndexName)), instanceData);
    }

    public static boolean updateOrInsertOneToCim(String kindName, Map<String, Object> instanceData, Map<String, Object> map) {
        List<Map<String, Object>> list = QueryToCim(kindName, map);
        List<Map<String, Object>> listMaps = new ArrayList<>();
        listMaps.add(instanceData);
        if (list != null) {
            if (list.size() >= 1) {
                updateToCim(kindName, (String) list.get(0).get(IndexName), instanceData);
                return true;
            }
        }
        System.out.print("save  " + kindName);
        saveToCim(kindName, listMaps);

        return true;
    }

    public static boolean removeAllToCim(String kindName, Map<String, Object> map) {
        List<Map<String, Object>> list = QueryToCim(kindName, map);
        for (int index = 0; index < list.size(); index++) {
            removeToCim(kindName, (String) list.get(index).get(IndexName));
        }
        return true;
    }
}
