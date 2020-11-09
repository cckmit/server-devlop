package com.glodon.pcop.cimstatsvc.task;

import com.glodon.pcop.cimstatsvc.config.SysConfigurations;
import com.glodon.pcop.cimstatsvc.task.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManage {
    public static  List<Task>  getTask(){
        List<Task> tasks = new ArrayList<>();
        //福州
        if(SysConfigurations.getDefaultTenantId().equals("1")) {
            tasks.addAll(getLaborManageTask());
            tasks.addAll(getQualityTask());
            tasks.addAll(getSafeTask());
        //青岛
        }else if(SysConfigurations.getDefaultTenantId().equals("2")){
            tasks.addAll(getIotTask());
        //新航城
        }else if(SysConfigurations.getDefaultTenantId().equals("3")){
            tasks.addAll(getCimTask());
        }
        return  tasks;
    }

    public static  List<Task>  getLaborManageTask(){
        List<Task> tasks = new ArrayList<>();
        Task managerCountTask = new Task();
        managerCountTask.setName("managerCount");
        managerCountTask.setCron("0 0 0 28 * ?");
        tasks.add(managerCountTask);

        Task workTeamCountTask = new Task();
        workTeamCountTask.setName("workTeamCount");
        workTeamCountTask.setCron("0 0 0/1 * * ?");
        tasks.add(workTeamCountTask);

        Task workTypeCountTask = new Task();
        workTypeCountTask.setName("workTypeCount");
        workTypeCountTask.setCron("0 0 0/1 * * ?");
        tasks.add(workTypeCountTask);

        Task laborCountTask = new Task();
        laborCountTask.setName("laborCount");
        laborCountTask.setCron("0 0 0 1 * ?");
        tasks.add(laborCountTask);

        return  tasks;

    }

    public static  List<Task>  getQualityTask(){
        List<Task> tasks = new ArrayList<>();


        Task qualityCountTask = new Task();
        qualityCountTask.setName("qualityCount");
        qualityCountTask.setCron("0 0 0/1 * * ?");
        tasks.add(qualityCountTask);


        Task qualityCountByDayTask = new Task();
        qualityCountByDayTask.setName("qualityCountByDay");
        qualityCountByDayTask.setCron("0 0 0 1/1 * ?");
        tasks.add(qualityCountByDayTask);


        return  tasks;
    }

    public static  List<Task>  getSafeTask(){
        List<Task> tasks = new ArrayList<>();

        Task safeCountTask = new Task();
        safeCountTask.setName("safeCount");
        safeCountTask.setCron("0 0 0/1 * * ?");
        tasks.add(safeCountTask);

        Task safeCountByDayTask = new Task();
        safeCountByDayTask.setName("safeCountByDay");
        safeCountByDayTask.setCron("0 0 0 1/1 * ?");
        tasks.add(safeCountByDayTask );

        return  tasks;
    }


    public static  List<Task>  getIotTask(){
        List<Task> tasks = new ArrayList<>();
        Task workloadTask = new Task();
        workloadTask.setName("workload");
        workloadTask.setCron("0 5 0/2 * * ? ");
        tasks.add(workloadTask);
        return  tasks;
    }

    public static  List<Task>  getCimTask(){
        List<Task> tasks = new ArrayList<>();

        Task workloadTask = new Task();
        workloadTask.setName("dataStructCount");
        workloadTask.setCron("0 0 18 * * ?");
        tasks.add(workloadTask);
        return  tasks;
    }

}
