package com.glodon.pcop.cimstatsvc.task;

import com.glodon.pcop.cimstatsvc.statistic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskExecute implements Runnable {
    private static final Logger mLog = LoggerFactory.getLogger(TaskExecute.class);

    private Task task;

    public TaskExecute(Task job) {
        this.task = job;
    }

    @Override
    public void run() {
        //管理人员统计
        if (task.getName().equals("managerCount")) {
            ManagerAttendanceStatistic.count();
        }
        //按劳务队伍统计
        if (task.getName().equals("workTeamCount")) {
            LaborWorkTeamStatistic.count();
        }
        //按工种类型统计
        if (task.getName().equals("workTypeCount")) {
            LaborWorkTypeStatistic.count();
        }
        //劳务队伍按类型统计
        if (task.getName().equals("laborCount")) {
            LaborAttendanceStatistic.count();
        }

        //质量统计
        if (task.getName().equals("qualityCount")) {
            QualityStatistic.count();
        }
        //质量按天统计
        if (task.getName().equals("qualityCountByDay")) {
            QualityStatistic.countByDay();
        }

        //安全统计
        if (task.getName().equals("safeCount")) {
            SafeStatistic.count();
        }
        //安全按天统计
        if (task.getName().equals("safeCountByDay")) {
            SafeStatistic.countByDay();
        }


        //指标统计
        if (task.getName().equals("workload")) {
            WorkLoadStatistic.count();
        }


        //结构数据统计
        if (task.getName().equals("dataStructCount")) {
            DataStructStatistic.count();
        }



        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        mLog.info(task.getName()+" exec end time :" + df.format(new Date()));// new Date()为获取当前系统时间
    }
}