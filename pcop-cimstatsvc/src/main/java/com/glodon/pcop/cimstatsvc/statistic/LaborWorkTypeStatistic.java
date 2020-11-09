package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.sql.Sql;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.removeAllToCim;
import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.updateOrInsertOneToCim;
import static com.glodon.pcop.cimstatsvc.dao.DbExecute.dbName;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayBegin;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayEnd;
import static com.glodon.pcop.cimstatsvc.util.DateUtils.getDayRange;

//
public class LaborWorkTypeStatistic {
    private static String workType = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TYPE;
    private static String attendSummary = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_ATTEND_SUMMARY;
    private static String attendSummaryWorkType = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_ATTEND_WORKTYPE;
    private static String  getProjectIdSql = "select distinct projectId from `" + attendSummary + "`";
    //统计每天项目的工种人数
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
        HashMap<String, JSONObject> teamMap = new HashMap<>();
        List<String> list = DbExecute.executeQuery(cds,getProjectIdSql) ;
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            count(cds,obj.getString("projectId"));
        }
        return "";
    }

    private static String getWorkName(String workType) {
        List<Map<String, Object>> workTypeList = GlobalDimensionFeatures.listGlobalDimensionItems(dbName, "laborTypes");
        for (int i = 0; i < workTypeList.size(); i++) {
            if (workTypeList.get(i).get("key").toString().equals(workType)) {
                return workTypeList.get(i).get("value").toString();
            }
        }
        return "其他";
    }

    private static String count(CimDataSpace cds,String projectId) {
        List<String> list = DbExecute.executeQuery(cds,getResSql(projectId));
        if (list.size() == 0) {
            return "";
        }
        List<Map<String, Object>> workTypeMap = GlobalDimensionFeatures.listGlobalDimensionItems(dbName, "laborTypes");

        List<String> projectIds = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            HashMap<String, Object> queryMap = new HashMap<>();
            queryMap.put("projectId", projectId);
            if (!projectIds.contains(projectId)) {
                removeAllToCim(ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TYPE, queryMap);
                projectIds.add(projectId);
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("projectId", projectId);
            map.put("workTypeId", obj.getString("workTypeId"));
            map.put("workTypeName", obj.getString("workTypeName"));
            if (map.get("workTypeName").toString().equals("null")) {
                map.put("workTypeName", getWorkName(obj.getString("workTypeId")));
            }

            map.put("onlineNumber", obj.getInt("attendCount"));
            map.put("recordDate", (new Date()).getTime());
            queryMap.put("workTypeId", obj.getString("workTypeId"));
            try {
                updateOrInsertOneToCim(ObjectTypeIdConstant.LABOR_REALTIME_ANALYSE_WORK_TYPE, map, queryMap);
            } catch (DataServiceUserException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static String getResSql(String projectId) {
        Sql sql = new Sql();
        sql.setSelect("workTypeId, workTypeName, SUM(attendCount) as attendCount");
        sql.setFrom("`" + attendSummaryWorkType + "`");
        sql.setGroup("workTypeId");
        sql.setWhere("summaryId in (" + getSummaryId(projectId) + ")"); //
        sql.setOrder("attendCount desc");
        System.out.println(sql.getSql());
        return sql.getSql();

    }

    public static String getSummaryId(String projectId) {
        Sql sql = new Sql();
        sql.setSelect("summaryId");
        sql.setFrom("`" + attendSummary + "`");
        sql.setWhere("projectId = " + projectId + "and checkDate " + getDayRange());
        System.out.println(sql.getSql());
        return sql.getSql();
    }

}
