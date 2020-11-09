package com.glodon.pcop.cimstatsvc.task;

import java.util.concurrent.ScheduledFuture;

public class Task {
    private String name;
    private String cron;
    private String desc;
    private ScheduledFuture<?> future;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }


}
