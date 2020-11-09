package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.sql.Sql;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.removeAllToCim;
import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.updateOrInsertOneToCim;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayBegin;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayEnd;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayRange;

//劳务队伍
public class LaborWorkTeamStatistic {
    private static String unit = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.SUB_CONSTRATOR;     // 分包商
    private static String team = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.SUB_CONSTRATOR_TEAM;    // 分包队伍
    private static String laborAttendSummary = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_ATTEND_SUMMARY;//劳务人员考勤概述
    private static String workTeam = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TEAM; //统计每天项目的队伍人数    // 采取覆盖的办法

    private static String unitSql = "select *  from `" +unit +"`";
    private static String workTeamSql = "select projectId,unitName as unitId,sum(attendCount) as attendCount from `" +laborAttendSummary +"` where checkDate  " + getDayRange() + "  group by  projectId,unitName";



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

    public static String countImpl(CimDataSpace cds) {
        //取出相关的队伍

        HashMap<String, JSONObject> teamMap = new HashMap<>();
        List<String> projectIds = new ArrayList<>();

        List<String> subList = DbExecute.executeQuery(cds,unitSql);
        for (int i = 0; i < subList.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(subList.get(i));
            teamMap.put(obj.getString("unitId") + obj.getString("projectId"), obj);
            if (!projectIds.contains(obj.getString("projectId"))) {
                projectIds.add(obj.getString("projectId"));
            }
        }

        List<String> list = DbExecute.executeQuery(cds,workTeamSql);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            HashMap<String, Object> map = new HashMap<>();
            JSONObject team = teamMap.get(obj.getString("unitId") + obj.getString("projectId"));

            HashMap<String, Object> queryMap = new HashMap<>();
            queryMap.put("projectId", obj.getString("projectId"));
            if (projectIds.contains(obj.getString("projectId"))) {
                removeAllToCim(ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TEAM, queryMap);
                projectIds.remove(obj.getString("projectId"));
            }
            map.put("projectId", obj.getString("projectId"));
            map.put("leaderId", team.getString("legalConcactor") + "-" + team.getString("unitName"));
            map.put("teamName", team.getString("legalConcactor"));
            map.put("onlineNumber", obj.getInt("attendCount"));
            map.put("recordDate", (new Date()).getTime());

            queryMap.put("teamName", map.get("teamName"));

            try {
                updateOrInsertOneToCim(ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TEAM, map, queryMap);
            } catch (DataServiceUserException e) {
                e.printStackTrace();
            }
        }

        LaborOverAllStatistic.count();
        return "";
    }
}
