package com.glodon.pcop.cimsvc.model.gis;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "通用时间查询输入条件")
public class GeneralTemporalQueryConditionInputBean {
    private String dataSetName;
    private String property;
    private Long startTimestamp;
    private Long endTimestamp;

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
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
        return "GeneralTemporalQueryConditionInputBean{" +
                "dataSetName='" + dataSetName + '\'' +
                ", property='" + property + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }
}
