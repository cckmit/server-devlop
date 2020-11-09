package com.glodon.pcop.cimstatsvc.dao;

import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimstatsvc.model.LaborCountByMonth;

import java.util.HashMap;
import java.util.Map;

public class LaborDao extends  BaseExDao{
     public static void saveLaborCount(LaborCountByMonth count){
         String kindName = count.getCimName();
         Map<String, Object> map = object2Map(count);
         Map<String, Object> queryMap = new HashMap<>();
         queryMap.put("projectId",count.getProjectId());
         queryMap.put("countMonth",count.getCountMonth());
         try {
             updateOrInsertOneToCim(kindName,map,queryMap);
         } catch (DataServiceUserException e) {
             e.printStackTrace();
         }
     }

}
