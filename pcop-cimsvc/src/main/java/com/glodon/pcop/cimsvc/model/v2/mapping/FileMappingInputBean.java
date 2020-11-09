package com.glodon.pcop.cimsvc.model.v2.mapping;

import java.util.List;

public class FileMappingInputBean {
    private String fileDataId;
    private String fileContentType;
    private List<ObjectMappingInputBean> fileMapping;

    public String getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(String fileDataId) {
        this.fileDataId = fileDataId;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public List<ObjectMappingInputBean> getFileMapping() {
        return fileMapping;
    }

    public void setFileMapping(List<ObjectMappingInputBean> fileMapping) {
        this.fileMapping = fileMapping;
    }

    @Override
    public String toString() {
        return "FileMappingInputBean{" +
                "fileDataId='" + fileDataId + '\'' +
                ", fileContentType='" + fileContentType + '\'' +
                ", fileMapping=" + fileMapping +
                '}';
    }
}
