package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.updateOrInsertOneToCim;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayRange;

//统计劳务管理的
//重大项目人员管理 --- 展示页面
//注册人数与实时人数
//注册人数 =  累计在场人数 -  累计出场人数
//实时人数 =  实时队伍人数之和
public class LaborOverAllStatistic {

    private static String regisNumberSql = "select projectId,projectName,(sum(approachCount)-sum(exitCount)) as regisNumber from `" + CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_INOUT_SUMMARY + "`   group  by projectId";
    private static String onlineNumberSql = "select projectId,projectName,sum(onlineNumber) as onlineNumber from `" + CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TEAM + "` where  recordDate  " + getDayRange() + "  group by  projectId";

    public static String count() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(DbExecute.dbName);
        try {
            countImpl(cds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return "";
    }


    public static void countImpl(CimDataSpace cds) {
        HashMap<String, String> projectId2name = new HashMap<>();
        HashMap<String, Integer> projectId2regis = new HashMap<>();
        HashMap<String, Integer> projectId2online = new HashMap<>();
        List<String> projectIds = new ArrayList<>();
        List<String> regisList = DbExecute.executeQuery(cds,regisNumberSql);
        for (int i = 0; i < regisList.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(regisList.get(i));
            String projectId = obj.getString("projectId");
            if (!projectIds.contains(projectId)) {
                projectIds.add(projectId);
            }

            projectId2name.put(obj.getString("projectId"), obj.getString("projectName"));
            projectId2regis.put(obj.getString("projectId"), (Integer) obj.get("regisNumber"));
        }
        List<String> onlineList = DbExecute.executeQuery(cds,onlineNumberSql);
        for (int i = 0; i < onlineList.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(onlineList.get(i));
            String projectId = obj.getString("projectId");
            if (!projectIds.contains(projectId)) {
                projectIds.add(projectId);
            }
            if(obj.getString("projectName") != "null") {
                projectId2name.put(obj.getString("projectId"), obj.getString("projectName"));
            }
            projectId2online.put(obj.getString("projectId"), (Integer) obj.get("onlineNumber"));
        }
        for (int i = 0; i < projectIds.size(); i++) {
            String projectId = projectIds.get(i);
            HashMap<String, Object> map = new HashMap<>();
            HashMap<String, Object> queryMap = new HashMap<>();
            queryMap.put("projectId", projectId);
            map.put("projectId", projectId);
            map.put("projectName", projectId2name.get(projectId));
            map.put("recordDate", new Date());

            if (projectId2regis.get(projectId) != null) {
                if (projectId2regis.get(projectId) > 0) {
                    map.put("regisNumber", projectId2regis.get(projectId));
                } else {
                    map.put("regisNumber", 0);
                }
            } else {
                map.put("regisNumber", 0);
            }

            if (projectId2online.get(projectId) != null) {
                if (projectId2online.get(projectId) > 0) {
                    map.put("onlineNumber", projectId2online.get(projectId));
                } else {
                    map.put("onlineNumber", 0);
                }
            } else {
                map.put("onlineNumber", 0);
            }

            try {
                updateOrInsertOneToCim(ObjectTypeIdConstant.LABOR_OVER_ALL, map, queryMap);
            } catch (DataServiceUserException e) {
                e.printStackTrace();
            }
        }
    }
}


