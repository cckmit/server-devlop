package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.dao.LaborDao;
import com.glodon.pcop.cimstatsvc.model.LaborCountByMonth;
import net.sf.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


//录入的劳务人员考勤统计
//遍历laborAttendSummary考勤概要
//按项目和单位名称统计出来`

public class LaborAttendanceStatistic {
    private static String modelName = CimDataEngineConstant.CLASSPERFIX_IHFACT + ObjectTypeIdConstant.LABOR_INOUT_SUMMARY;
    private static String statSql = "select projectId,projectName, recordDate.format(\"yyyy-MM\").asString() as month, countProject(exitCount), countProject(approachCount) from `" + modelName + "` group by projectId  order by projectId,recordDate.format(\"yyyy-MM\").asString()";


    public static Date strToDate(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String dateToStr(Date strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(strDate);
        return dateString;
    }

    /**
     * 获取某个时间段内所有月份
     *
     * @param minDate
     * @param maxDate
     * @return
     */
    public static List<String> getMonthBetweenDates(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<String>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(strToDate(minDate, "yyyy-MM"));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(strToDate(maxDate, "yyyy-MM"));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(dateToStr(curr.getTime(), "yyyy-MM"));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    public static void main(String args[]) {
        List<String> ss = getMonthBetweenDates("2017-01", "2017-01");
    }


//    approachCount: 137
//    countMonth: "2019-03"
//    exitCount: 0
//    projectId: "3501921811010101"
//    recorderDate: 1551398400000
    public static void count1() {
        List<String> list = DbExecute.executeQuery(statSql);

        HashMap<String, HashMap<String, Object>> map = new HashMap<>();
        HashMap<String, String> maxDate = new HashMap<>();  //最大日期
        HashMap<String, String> minDate = new HashMap<>();  //最小日期
        HashMap<String, List<String>> projectDateArrayMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            String projectId = obj.getString("projectId");
            String monthStr = obj.getString("monthStr");
            HashMap<String, Object> item = new HashMap<>();
            item.put("projectId",projectId);
            item.put("countMonth",monthStr);
            item.put("approachCount",obj.getInt("approachCount"));
            item.put("exitCount",obj.getInt("exitCount"));


            if (map.get(projectId) == null) {
                map.put(projectId, new HashMap<>());
            }
            map.get(projectId).put(monthStr, item);

            if (maxDate.get(projectId) == null) {
                maxDate.put(projectId, monthStr);
            } else {
                if (maxDate.get(projectId).compareTo(monthStr) < 0) {
                    maxDate.put(projectId, monthStr);
                }
            }
            if (minDate.get(projectId) == null) {
                minDate.put(projectId, monthStr);
            } else {
                if (minDate.get(projectId).compareTo(monthStr) > 0) {
                    minDate.put(projectId, monthStr);
                }
            }
        }


        for (String key : map.keySet()) {
            if (projectDateArrayMap.get(key) == null) {
                projectDateArrayMap.put(key, getMonthBetweenDates(minDate.get(key), maxDate.get(key)));
            }
        }

        List<Object>  data = new ArrayList<>();
        for(String projectId:projectDateArrayMap.keySet()){
            List<String>  monthStrs = projectDateArrayMap.get(projectId);
            for(int i= 0;i<monthStrs.size();i++){
                String countMonth = monthStrs.get(i);
                if(map.get(projectId).get(countMonth) != null){
                    data.add(map.get(projectId).get(countMonth));
                }else{
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("projectId",projectId);
                    item.put("countMonth",countMonth);
                    item.put("approachCount",0);
                    item.put("exitCount",0);
                    data.add(item);
                }
            }
        }

        //存储数据
        for (int i = 0; i < list.size(); i++) {


        }

    }
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
        List<String> list = DbExecute.executeQuery(cds,getSql());
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject().fromObject(list.get(i));
            LaborCountByMonth labor = (LaborCountByMonth) JSONObject.toBean(obj, LaborCountByMonth.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String str = sdf.format(c.getTime()); //上月
            labor.setCountMonth(str);
            labor.setRecorderDate(new Date());
            labor.setRecorderName("cimLaborCount");
            labor.setRecorderId(labor.getProjectId() + str);
            LaborDao.saveLaborCount(labor);
        }
    }

    private static String getSql() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String gtimelast = sdf.format(c.getTime()); //上月
        System.out.println(gtimelast);
        int lastMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(lastMonthMaxDay);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);

        //按格式输出
        String gtime = sdf.format(c.getTime()); //上月最后一天
        System.out.println(gtime);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-01  00:00:00");
        String gtime2 = sdf2.format(c.getTime()); //上月第一天
        System.out.println(gtime2);

        String sql = "SELECT projectId, sum(exitCount) as exitCount, sum(approachCount) as approachCount"
                + " FROM " + modelName
                + " WHERE checkDate BETWEEN 'STARTTIME' and 'ENDTIME'" +
                "group by `projectId`";
        sql = sql.replace("STARTTIME", gtime2);
        sql = sql.replace("ENDTIME", gtime);
        System.out.println(sql);
        return sql;
    }
}
