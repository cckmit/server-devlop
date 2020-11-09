package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "时间查询输入条件")
public class TemporalQueryConditionInputBean {
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

    @Override
    public String toString() {
        return "TemporalQueryConditionInputBean{" +
                "startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }
}
