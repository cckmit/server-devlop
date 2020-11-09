package com.glodon.pcop.cimsvc.model.gis;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "通用空间查询输入条件")
public class GeneralSpatialQueryConditionInputBean {
    private String dataSetName;
    private String property;
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

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "GeneralSpatialQueryConditionInputBean{" +
                "dataSetName='" + dataSetName + '\'' +
                ", property='" + property + '\'' +
                ", queryTypeEnum=" + queryTypeEnum +
                ", boundary='" + boundary + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    public enum GisQueryTypeEnum {
        CIRCLE, POLYGON
    }

}
