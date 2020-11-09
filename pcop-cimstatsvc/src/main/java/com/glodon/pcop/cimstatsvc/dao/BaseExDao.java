package com.glodon.pcop.cimstatsvc.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;

import java.io.IOException;
import java.util.*;

public class BaseExDao extends  DbExecute {
    protected static String IndexName = "INFO_OBJECT_ID";
    protected static String defaultTenantProperty = "CIM_BUILDIN_TENANT_ID";
    public static String defaultTenantId = "CIM_BUILDIN_PUBLIC_TENANT";
    public static String objectTypeId = "projectV1";

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


    //查询对象模型定义
    public static InfoObjectDef getInfoObjectDef(String tenantId, String infoObjectTypeName) {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(dbName, tenantId);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectTypeName);
        if (infoObjectDef != null) {
            return infoObjectDef;
        }
        cimModelCore = ModelAPIComponentFactory.getCIMModelCore(dbName,"1");
        infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectTypeName);
        if (infoObjectDef != null) {
            return infoObjectDef;
        }
        cimModelCore = ModelAPIComponentFactory.getCIMModelCore(dbName,"2");
        infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectTypeName);
        if (infoObjectDef != null) {
            return infoObjectDef;
        }
        cimModelCore = ModelAPIComponentFactory.getCIMModelCore(dbName,defaultTenantId);
        infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectTypeName);
        if (infoObjectDef != null) {
            return infoObjectDef;
        }


        return null;
    }

    public static void saveToCim(String kindName, List<Map<String, Object>> listMaps) {
        List<InfoObjectValue> objectValueList = new ArrayList<>();
        for (Map<String, Object> map : listMaps) {
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, Object> baseMap = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    if (key.toUpperCase().equals("ID")) {
                        baseMap.put("ID", map.get(key));
                    }else if(map.get(key) instanceof Date){
                        dataMap.put(key, ((Date)map.get(key)).getTime());
                    } else {
                        dataMap.put(key, map.get(key));
                    }
                }
            }
            //导入模型
            baseMap.put("objectTypeId", kindName);
            InfoObjectValue infoObjectValue = new InfoObjectValue();
            infoObjectValue.setGeneralDatasetsPropertiesValue(dataMap);
            infoObjectValue.setBaseDatasetPropertiesValue(baseMap);
            objectValueList.add(infoObjectValue);

        }
        String tenantId = getTenantIdByInstance(listMaps.get(0));
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);
        infoObjectDef.newObjects(objectValueList, false);

        return;
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
        ep.setPageSize(10000);
        String tenantId = getTenantIdByInstance(map);
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);
        InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(ep);
        List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
        List<Map<String, Map<String, Object>>> result1 = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject infoObject : infoObjectList) {
                Map<String, Object> objectValuesMap = new HashMap<>();
                Map tmpMap = null;
                try {
                    tmpMap = infoObject.getObjectPropertiesByDatasets();
                    for (Object key : tmpMap.keySet()) {
                        objectValuesMap.putAll((Map<String, Object>) tmpMap.get(key));
                    }
                } catch (DataServiceModelRuntimeException e) {
                    e.printStackTrace();
                }
                // tmpMap.put(IndexName, infoObject.getObjectInstanceRID());
                objectValuesMap.put(IndexName, infoObject.getObjectInstanceRID());
                result.add(objectValuesMap);
            }
        }
        return result;
    }

    public static boolean removeToCim(String kindName, String objectId) {
        String tenantId = getTenantIdByInstance(null);
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);
        return infoObjectDef.deleteObject(objectId);
    }

    public static boolean removeToCim(String kindName, Map<String, Object> instanceData) {
        String tenantId = getTenantIdByInstance(instanceData);
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);
        return infoObjectDef.deleteObject((String) instanceData.get(IndexName));
    }

    public static boolean updateToCim(String kindName, String objectId, Map<String, Object> instanceData) throws DataServiceUserException {
        Map<String, Object> data = new HashMap<>();
        for (String key : instanceData.keySet()) {
            if(instanceData.get(key) !=null ){
                if(instanceData.get(key) instanceof Date){
                    data.put(key, ((Date) instanceData.get(key)).getTime());
                }else{
                    data.put(key, instanceData.get(key));
                }
            }
        }

        String tenantId = getTenantIdByInstance(data);
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);
        InfoObjectValue infoObjectValue = new InfoObjectValue();
        infoObjectValue.setObjectInstanceRID(objectId);
        infoObjectValue.setGeneralDatasetsPropertiesValue(data);
        infoObjectDef.updateObject(infoObjectValue);
        return true;
    }

    public static boolean updateToCim(String kindName, Map<String, Object> instanceData) {
        if (((String) instanceData.get(IndexName)).equals("")) {
            return false;
        }
        return InfoObjectFeatures.updateInfoObjectInstanceData(dbName, kindName, ((String) instanceData.get(IndexName)), instanceData);
    }

    //
    public static boolean updateOrInsertOneToCim(String kindName, Map<String, Object> instanceData, Map<String, Object> map) throws DataServiceUserException {
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

    // 删除所有对象
    public static boolean removeAllToCim(String kindName, Map<String, Object> map) {
        List<Map<String, Object>> list = QueryToCim(kindName, map);
        String tenantId = getTenantIdByInstance(map);
        InfoObjectDef infoObjectDef = getInfoObjectDef(tenantId, kindName);

        for (int index = 0; index < list.size(); index++) {
            infoObjectDef.deleteObject((String) list.get(index).get(IndexName));
        }
        return true;
    }


    //获取项目对应的TenantId
    public static String getTenantIdByInstance( Map<String, Object> map){
        String tenantId  = defaultTenantId;
        if (map == null) {
            return tenantId;
        }
        if (map.get("projectId") == null) {
            return tenantId;
        }
        CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
        try {
            InformationExplorer ie = cimDataSpace.getInformationExplorer();
            ExploreParameters ep = new ExploreParameters();
            ep.setType(objectTypeId);
            EqualFilteringItem equalFilteringItem = new EqualFilteringItem("projectId", map.get("projectId"));
            ep.setDefaultFilteringItem(equalFilteringItem);
            try {
                List<Fact> projectFactList = ie.discoverInheritFacts(ep);
                if (projectFactList != null) {
                    for (Fact currentFact : projectFactList) {
                        List<Relation> ss = currentFact.getAllSpecifiedRelations(BusinessLogicConstant.RELATION_TYPE_BELONGS_TO_TENANT, RelationDirection.FROM);
                        tenantId = (String) ss.get(0).getToRelationable().getProperty(defaultTenantProperty).getPropertyValue();
                        break;
                    }
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            } catch (CimDataEngineInfoExploreException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cimDataSpace != null) {
                cimDataSpace.closeSpace();
            }
        }
        return tenantId;
    }
}
