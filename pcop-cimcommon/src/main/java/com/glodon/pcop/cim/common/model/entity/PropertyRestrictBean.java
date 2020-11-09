package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "dataSetId", "propertyId", "isNull", "isPrimaryKey", "defaultValue","length","isUniqueKey"})
public class PropertyRestrictBean {
    private String id;
    private String dataSetId;
    private String propertyId;
    private Boolean isNull;
    private Boolean isPrimaryKey;
    private String defaultValue;
    private Boolean editable;
    private long length;
    private Boolean isUniqueKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_null")
    public Boolean isNull() {
        return isNull;
    }

    @JsonProperty("is_null")
    public void setNull(Boolean isNull) {
        this.isNull = isNull;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
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
}
