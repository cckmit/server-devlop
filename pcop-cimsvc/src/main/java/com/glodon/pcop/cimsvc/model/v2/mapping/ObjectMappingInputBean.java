package com.glodon.pcop.cimsvc.model.v2.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder(value = {"singleFileName", "objectTypeId", "objectTypeName", "isCreate", "isClean", "dataSets"},
        alphabetic = false)
public class ObjectMappingInputBean {
    private String singleFileName;
    private String objectTypeId;
    private String objectTypeName;
    private Boolean isCreate;
    private Boolean isClean;
    private List<DataSetMappingInputBean> dataSets;

    private Boolean isUpdate;

    public String getSingleFileName() {
        return singleFileName;
    }

    public void setSingleFileName(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    @JsonProperty("is_create")
    public Boolean getCreate() {
        return isCreate;
    }

    @JsonProperty("is_create")
    public void setCreate(Boolean create) {
        isCreate = create;
    }

    @JsonProperty("is_clean")
    public Boolean getClean() {
        return isClean;
    }

    @JsonProperty("is_clean")
    public void setClean(Boolean clean) {
        isClean = clean;
    }

    @JsonProperty("is_update")
    public Boolean getUpdate() {
        return isUpdate;
    }

    @JsonProperty("is_update")
    public void setUpdate(Boolean update) {
        isUpdate = update;
    }

    public List<DataSetMappingInputBean> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<DataSetMappingInputBean> dataSets) {
        this.dataSets = dataSets;
    }

    @Override
    public String toString() {
        return "ObjectMappingInputBean{" +
                "singleFileName='" + singleFileName + '\'' +
                ", objectTypeId='" + objectTypeId + '\'' +
                ", objectTypeName='" + objectTypeName + '\'' +
                ", isCreate=" + isCreate +
                ", isClean=" + isClean +
                ", isUpdate=" + isUpdate +
                ", dataSets=" + dataSets +
                '}';
    }
}
