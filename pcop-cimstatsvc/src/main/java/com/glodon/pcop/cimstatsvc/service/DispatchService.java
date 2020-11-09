package com.glodon.pcop.cimstatsvc.service;

import com.glodon.pcop.cimstatsvc.task.Task;
import com.glodon.pcop.cimstatsvc.task.TaskExecute;
import com.glodon.pcop.cimstatsvc.task.TaskManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {
    private static final Logger mLog = LoggerFactory.getLogger(DispatchService.class);
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;



    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }



    public  void dispath(){
        List<Task> tasks = TaskManage.getTask();
        threadPoolTaskScheduler =  new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.initialize();
        for(int i = 0;i< tasks.size();i++) {
            Task task = tasks.get(i);
            threadPoolTaskScheduler.schedule(new TaskExecute(task),new CronTrigger(task.getCron()));
        }
    }

}
