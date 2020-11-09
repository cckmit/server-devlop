package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Gis空间查询输入条件")
public class GisSpatialQueryConditionInputBean {
    private GisQueryTypeEnum queryTypeEnum;
    private String boundary;
    private String output;

    public GisQueryTypeEnum getQueryTypeEnum() {
        return queryTypeEnum;
    }

    public void setQueryTypeEnum(GisQueryTypeEnum queryTypeEnum) {
        this.queryTypeEnum = queryTypeEnum;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "GisSpatialQueryConditionInputBean{" +
                "queryTypeEnum=" + queryTypeEnum +
                ", boundary='" + boundary + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    public enum GisQueryTypeEnum {
        CIRCLE, POLYGON
    }

}
