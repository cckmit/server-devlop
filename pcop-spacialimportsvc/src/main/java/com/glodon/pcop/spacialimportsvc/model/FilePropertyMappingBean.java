package com.glodon.pcop.spacialimportsvc.model;

import java.util.Map;
import java.util.Set;

public class FilePropertyMappingBean {
    private String id;
    private String taskId;
    private String tenantId;
    private String objectTypeId;
    private String dataSetId;
    private String dataSetName;
    private String fileDataId;
    private String singleFileName;

    // private Map<String, String> propertyMapping;
    private Map<String, Set<String>> propertyMapping;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(String fileDataId) {
        this.fileDataId = fileDataId;
    }

    public String getSingleFileName() {
        return singleFileName;
    }

    public void setSingleFileName(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public Map<String, Set<String>> getPropertyMapping() {
        return propertyMapping;
    }

    public void setPropertyMapping(Map<String, Set<String>> propertyMapping) {
        this.propertyMapping = propertyMapping;
    }
}
