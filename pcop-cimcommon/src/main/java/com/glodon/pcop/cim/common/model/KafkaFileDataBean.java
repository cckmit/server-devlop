package com.glodon.pcop.cim.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class KafkaFileDataBean {
    @JsonProperty("task_id")
    private String taskId;
    private Map<String, String> data;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
