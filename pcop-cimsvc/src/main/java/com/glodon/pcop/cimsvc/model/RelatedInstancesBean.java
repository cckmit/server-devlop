package com.glodon.pcop.cimsvc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class RelatedInstancesBean {

    public RelatedInstancesBean(String objectTypeName, String instanceId, Map<String, Map<String, Object>> dataSetValue) {
        this.objectTypeName = objectTypeName;
        this.dataSetValue = dataSetValue;
        this.infoObjectId = instanceId;
    }

    public RelatedInstancesBean() {
    }

    private String objectTypeName;
    private String infoObjectId;
    private Map<String, Map<String, Object>> dataSetValue;

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    @JsonProperty("INFO_OBJECT_ID")
    public String getInfoObjectId() {
        return infoObjectId;
    }

    @JsonProperty("INFO_OBJECT_ID")
    public void setInfoObjectId(String infoObjectId) {
        this.infoObjectId = infoObjectId;
    }

    public Map<String, Map<String, Object>> getDataSetValue() {
        return dataSetValue;
    }

    public void setDataSetValue(Map<String, Map<String, Object>> dataSetValue) {
        this.dataSetValue = dataSetValue;
    }
}
