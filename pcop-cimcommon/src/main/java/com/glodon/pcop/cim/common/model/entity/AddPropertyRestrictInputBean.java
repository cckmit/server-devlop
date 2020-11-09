package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"dataSetId", "propertyId", "isNull", "isPrimaryKey", "defaultValue"})
public class AddPropertyRestrictInputBean {
    private Boolean isNull;
    private Boolean isPrimaryKey;
    private String defaultValue;
    private Boolean editable;
    private long length;
    private Boolean isUniqueKey;

    @JsonProperty("is_null")
    public Boolean isNull() {
        return isNull;
    }

    @JsonProperty("is_null")
    public void setNull(Boolean isNull) {
        this.isNull = isNull;
    }

    @JsonProperty("is_primary")
    public Boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    @JsonProperty("is_primary")
    public void setPrimaryKey(Boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @JsonProperty("is_unique")
    public Boolean isUniqueKey() {
        return isUniqueKey;
    }
    @JsonProperty("is_unique")
    public void setUniqueKey(Boolean uniqueKey) {
        isUniqueKey = uniqueKey;
    }


    @Override
    public String toString() {
        return "AddPropertyRestrictInputBean{" +
                "isNull=" + isNull +
                ", isPrimaryKey=" + isPrimaryKey +
                ", defaultValue='" + defaultValue + '\'' +
                ", editable=" + editable +
                '}';
    }
}
