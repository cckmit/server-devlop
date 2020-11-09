package com.glodon.pcop.cimstatsvc.dao;

import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimstatsvc.model.ManagerAttendDetailCountByMonth;
import com.glodon.pcop.cimstatsvc.model.ManagerAttendSummaryCountByMonth;

import java.util.HashMap;
import java.util.Map;

public class MangerDao extends  BaseExDao{
    public static void saveLaborCount(ManagerAttendDetailCountByMonth count){
        String kindName = count.getCimName();
        Map<String, Object> map = object2Map(count);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("projectId",count.getProjectId());
        queryMap.put("personId",count.getPersonId());
        queryMap.put("staticCycle",count.getStaticCycle());
        try {
            updateOrInsertOneToCim(kindName,map,queryMap);
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        }
    }
    public static void saveLaborCount(ManagerAttendSummaryCountByMonth count){
        String kindName = count.getCimName();
        Map<String, Object> map = object2Map(count);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("projectId",count.getProjectId());
        queryMap.put("staticCycle",count.getStaticCycle());
        try {
            updateOrInsertOneToCim(kindName,map,queryMap);
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        }
    }
}
