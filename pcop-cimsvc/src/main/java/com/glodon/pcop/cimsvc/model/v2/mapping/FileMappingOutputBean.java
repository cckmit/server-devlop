package com.glodon.pcop.cimsvc.model.v2.mapping;

public class FileMappingOutputBean {
    private String singleFileName;
    private String taskId;
    private String message;

    public FileMappingOutputBean(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public FileMappingOutputBean(String singleFileName, String taskId) {
        this.singleFileName = singleFileName;
        this.taskId = taskId;
        this.message = "success";
    }

    public String getSingleFileName() {
        return singleFileName;
    }

    public void setSingleFileName(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
