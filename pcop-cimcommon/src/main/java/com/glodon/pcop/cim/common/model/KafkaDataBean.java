package com.glodon.pcop.cim.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author : tangd
 * @data : 2019-04-23
 */
public class KafkaDataBean {
    private String count;

    //@JsonProperty("task_id")
    private String taskId;


    private String tenantId;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
