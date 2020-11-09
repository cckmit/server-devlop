package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "管理人员")
public class WorkerBean {

    public WorkerBean() {
        super();
    }

    public WorkerBean(String id, String personName, String personId, String duty, Integer leaveTimes,
                      Float attenceRate) {
        this.id = id;
        this.personName = personName;
        this.personId = personId;
        this.duty = duty;
        this.leaveTimes = leaveTimes;
        this.attenceRate = attenceRate;
    }

    @ApiModelProperty(value = "ID", required = false)
    private String id;

    @ApiModelProperty(value = "人员名称", required = true)
    private String personName;

    @ApiModelProperty(value = "人员ID", required = true)
    private String personId;

    @ApiModelProperty(value = "职务", required = true)
    private String duty;

    @ApiModelProperty(value = "请假次数", example = "123", required = true)
    private int leaveTimes;

    @ApiModelProperty(value = "出勤率", required = true)
    private Float attenceRate;

    public String getId() {
        return id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public Integer getLeaveTimes() {
        return leaveTimes;
    }

    public void setLeaveTimes(Integer leaveTimes) {
        this.leaveTimes = leaveTimes;
    }

    public Float getAttenceRate() {
        return attenceRate;
    }

    public void setAttenceRate(Float attenceRate) {
        this.attenceRate = attenceRate;
    }
}
