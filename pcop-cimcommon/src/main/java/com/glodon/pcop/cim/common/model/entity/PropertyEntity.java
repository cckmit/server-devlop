package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"id", "name", "desc", "creator","updator","dataType", "confItems", "restrictInfo", "createTime", "updateTime"})
public class PropertyEntity extends BaseEntity {
    private DataTypes dataType;
    private Map<String, Object> confItems;
    private PropertyRestrictBean restrictInfo;

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

    public PropertyRestrictBean getRestrictInfo() {
        return restrictInfo;
    }

    public void setRestrictInfo(PropertyRestrictBean restrictInfo) {
        this.restrictInfo = restrictInfo;
    }


    public enum DataTypes {BOOLEAN,
        INT,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
        DATE,
        STRING,
        BINARY,
        BYTE,
        DECIMAL,
        BOOLEAN_ARRAY,
        INT_ARRAY,
        SHORT_ARRAY,
        LONG_ARRAY,
        FLOAT_ARRAY,
        DOUBLE_ARRAY,
        DATE_ARRAY,
        STRING_ARRAY,
        BINARY_ARRAY,
        DECIMAL_ARRAY,
        MP3,
        SHP,
        TSDB,
        DWG,
        FILE}


}
