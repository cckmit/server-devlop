package com.glodon.pcop.cimsvc.model.v2.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;

import java.util.List;

public class DataSetMappingInputBean {
    private String id;
    private String desc;
    private String name;
    private Boolean isCreate;
    private List<PropertyMappingInputBean> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("is_create")
    public Boolean getCreate() {
        return isCreate;
    }

    @JsonProperty("is_create")
    public void setCreate(Boolean create) {
        isCreate = create;
    }

    public List<PropertyMappingInputBean> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyMappingInputBean> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "DataSetMappingInputBean{" +
                "id='" + id + '\'' +
                ", desc='" + desc + '\'' +
                ", name='" + name + '\'' +
                ", isCreate=" + isCreate +
                ", properties=" + properties +
                '}';
    }
}
