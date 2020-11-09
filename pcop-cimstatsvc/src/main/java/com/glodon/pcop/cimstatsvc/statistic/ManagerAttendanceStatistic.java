package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.dao.MangerDao;
import com.glodon.pcop.cimstatsvc.model.ManagerAttendDetailCountByMonth;
import com.glodon.pcop.cimstatsvc.model.ManagerAttendSummaryCountByMonth;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerAttendanceStatistic {
    //管理人员统计
    //1.总工作小时计算 = 按填报的概要记录总条数*8（小时）
    //2.考情周期为 上月26号——本月25号
    //3.本月28号定时去统计考勤周期（上月26号——本月25号）内的数据
    //统计数据来源表

    private static String projectModel = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.PROJECT;
    ;      //项目表
    private static String adminAttendSummaryModel = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.ADMIN_ATTEND_SUMMARY;
    private static String adminAttendDetailModel = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.ADMIN_ATTEND_DETAIL;

    //统计上个月的考情概要
    public static void ManagerAttendSummaryCoount(CimDataSpace cds) {
        String sqlProject = "select projectId,projectName " +
                "from `" + projectModel + "` " +
                "WHERE  projectId IN (select distinct projectId from `" + adminAttendSummaryModel + "`)";
        List<String> list = DbExecute.executeQuery(cds,sqlProject);
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            String projectId = obj.getString("projectId");
            String projectName = obj.getString("projectName");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 0);
            String str = sdf.format(c.getTime()); //本月
            ManagerAttendSummaryCountByMonth count = new ManagerAttendSummaryCountByMonth();
            count.setProjectId(projectId);
            count.setProjectName(projectName);
            count.setStaticCycle(str);

            projectCount(cds,projectId, projectName);
            MangerDao.saveLaborCount(count);
        }
    }

    //统计考勤详情
    public static void projectCount(CimDataSpace cds,String projectId, String projectName) {
        String subSql = subSql(projectId);
        //统计这个考勤内需要工作的时间
        String DutyTimeCount = "select countProject(summaryId) as allDutyday from `" + adminAttendSummaryModel + "` " + subSql;
        List<String> list = DbExecute.executeQuery(cds,DutyTimeCount);
        int day = 0;
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            day = obj.getInt("allDutyday");
            break;
        }

        Map<String, ManagerAttendDetailCountByMonth> countMap = new HashMap<>();

        //统计每个人的工作时长
        String sql = "select " +
                "personName," + "personPinID," + "sum(onDutyTime) as onDutyTime," + "dutyTypeName," + "countProject(isAbsence) as Absence  " +
                "from `" + adminAttendDetailModel + "` " +
                "where summaryId in (select summaryId from `" + adminAttendSummaryModel + "`" + subSql + " ) " +
                "group by personPinID";
        List<String> list1 = DbExecute.executeQuery(cds,sql);

        for (int i = 0; i < list1.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list1.get(i));
            ManagerAttendDetailCountByMonth count = new ManagerAttendDetailCountByMonth();
            count.setProjectId(projectId);
            count.setProjectName(projectName);
            count.setPersonId(obj.getString("personPinID"));
            count.setPersonName(obj.getString("personName"));
            count.setAttenceRate(obj.getInt("onDutyTime") / (double) (day * 8));
            count.setDuty(obj.getString("dutyTypeName"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 0);
            String str = sdf.format(c.getTime()); //本月
            count.setStaticCycle(str);
            countMap.put(obj.getString("personPinID"), count);
        }

        //统计每个人的请假次数
        String sql1 = "select " +
                "personName," + "personPinID," + "dutyTypeName," + "countProject(isAbsence) as absence " +
                "from `" + adminAttendDetailModel + "` " +
                "where summaryId in (select summaryId from `" + adminAttendSummaryModel + "`" + subSql + " ) and isAbsence = true  " +
                "group by personPinID";
        List<String> list2 = DbExecute.executeQuery(cds,sql1);
        for (int i = 0; i < list2.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list2.get(i));
            ManagerAttendDetailCountByMonth count = countMap.get(obj.getString("personPinID"));
            count.setLeaveCount(obj.getInt("absence"));
            countMap.put(obj.getString("personPinID"), count);
        }

        //保存结果到数据库
        Iterator entries = countMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            ManagerAttendDetailCountByMonth value = (ManagerAttendDetailCountByMonth) entry.getValue();
            MangerDao.saveLaborCount(value);
        }

    }


    public static String subSql(String projectId) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-25  23:59:59");
        String gtime1 = sdf.format(c.getTime());
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.MONTH, -1);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-26  00:00:00");
        String gtime2 = sdf2.format(c1.getTime()); //上月第一天
        System.out.println(gtime2);
        String subSql = "WHERE projectId = 'PROJECTID' AND checkDate BETWEEN 'STARTTIME' and 'ENDTIME'";
        subSql = subSql.replace("PROJECTID", projectId);
        subSql = subSql.replace("STARTTIME", gtime2);
        subSql = subSql.replace("ENDTIME", gtime1);
        System.out.println(subSql);
        return subSql;
    }

    public static void count() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(DbExecute.dbName);
        try {
            ManagerAttendSummaryCoount(cds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}
