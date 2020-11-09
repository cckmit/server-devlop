package com.glodon.pcop.cim.common.model;

public class FileUploadStatusBean {
    private boolean isSuccess = false;
    private boolean isOverwrite = false;
    private String objectName;
    private String msg;
    private String bucket;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isOverwrite() {
        return isOverwrite;
    }

    public void setOverwrite(boolean overwrite) {
        isOverwrite = overwrite;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
