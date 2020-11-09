package com.glodon.pcop.cimsvc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileMetadataUploadBean {
    @JsonProperty("fileDataId")
    private String fileDataId;
    @JsonProperty("fileDataName")
    private String fileDataName;
    @JsonProperty("srcFileName")
    private String srcFileName;
    @JsonProperty("instanceRid")
    private String instanceRid;
    @JsonProperty("minioObjectName")
    private String minioObjectName;

    @JsonProperty("comment")
    private String comment;
    @JsonProperty("contact")
    private String contact;
    @JsonProperty("contactNumber")
    private String contactNumber;

    public String getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(String fileDataId) {
        this.fileDataId = fileDataId;
    }

    public String getFileDataName() {
        return fileDataName;
    }

    public void setFileDataName(String fileDataName) {
        this.fileDataName = fileDataName;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public String getMinioObjectName() {
        return minioObjectName;
    }

    public void setMinioObjectName(String minioObjectName) {
        this.minioObjectName = minioObjectName;
    }
}
