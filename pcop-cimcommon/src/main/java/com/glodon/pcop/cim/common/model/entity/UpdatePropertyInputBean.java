package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"name", "desc", "creator", "dataType", "confItems", "restrictInfo"})
public class UpdatePropertyInputBean {
    private String id;
    private String name;
    private String desc;
    private DataTypes dataType;
    private Map<String, Object> confItems;
    private UpdatePropertyRestrictInputBean restrictInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DataTypes getDataType() {
        return dataType;
    }

    public void setDataType(DataTypes dataType) {
        this.dataType = dataType;
    }

    public Map<String, Object> getConfItems() {
        return confItems;
    }

    public void setConfItems(Map<String, Object> confItems) {
        this.confItems = confItems;
    }

    public UpdatePropertyRestrictInputBean getRestrictInfo() {
        return restrictInfo;
    }

    public void setRestrictInfo(UpdatePropertyRestrictInputBean restrictInfo) {
        this.restrictInfo = restrictInfo;
    }

    @Override
    public String toString() {
        return "AddPropertyInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", dataType=" + dataType +
                ", confItems=" + confItems +
                ", restrictInfo=" + restrictInfo +
                '}';
    }

    public enum DataTypes {STRING, SHORT, BYTE, INT, LONG, FLOAT, DOUBLE, BOOLEAN, DATE, BINARY}


}
