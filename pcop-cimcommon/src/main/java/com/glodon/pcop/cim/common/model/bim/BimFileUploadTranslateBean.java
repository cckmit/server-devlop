package com.glodon.pcop.cim.common.model.bim;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;

public class BimFileUploadTranslateBean {
    @NotNull
    private String bucket;
    private String fileName;
    @JsonIgnore
    private String objectId;
    @NotNull
    private String srcFileName;
    private String tenantId;

    public BimFileUploadTranslateBean(String bucket, String fileName, String objectId, String srcFileName,
            String tenantId) {
        this.bucket = bucket;
        this.fileName = fileName;
        this.objectId = objectId;
        this.srcFileName = srcFileName;
        this.tenantId = tenantId;
    }

    public BimFileUploadTranslateBean() {
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "BimFileUploadTranslateBean{" +
                "bucket='" + bucket + '\'' +
                ", fileName='" + fileName + '\'' +
                ", objectId='" + objectId + '\'' +
                ", srcFileName='" + srcFileName + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
