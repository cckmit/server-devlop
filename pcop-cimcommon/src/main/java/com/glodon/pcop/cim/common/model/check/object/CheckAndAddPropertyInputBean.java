package com.glodon.pcop.cim.common.model.check.object;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.glodon.pcop.cim.common.model.entity.AddPropertyRestrictInputBean;

import java.util.Map;

public class CheckAndAddPropertyInputBean {
    private String name;
    private String desc;
    private DataTypes dataType;

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

    @Override
    public String toString() {
        return "CheckAndAddPropertyInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", dataType=" + dataType +
                '}';
    }

    public enum DataTypes {STRING, SHORT, BYTE, INT, LONG, FLOAT, DOUBLE, BOOLEAN, DATE, BINARY}


}
