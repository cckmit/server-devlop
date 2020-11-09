package com.glodon.pcop.cimstatsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;

import java.util.Date;

public class LaborCountByMonth {
    private String projectId;//	string	项目id
    private String recorderId;    //string	记录id
    private String recorderName;    //string	记录人
    private Date recorderDate;    //date	记录时间
    private String countMonth;    //string	月份
    private int approachCount;//	int	进场人数
    private int exitCount;// 	int	退场人数

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
    }

    public Date getRecorderDate() {
        return recorderDate;
    }

    public void setRecorderDate(Date recorderDate) {
        this.recorderDate = recorderDate;
    }

    public String getCountMonth() {
        return countMonth;
    }

    public void setCountMonth(String countMonth) {
        this.countMonth = countMonth;
    }

    public int getApproachCount() {
        return approachCount;
    }

    public void setApproachCount(int approachCount) {
        this.approachCount = approachCount;
    }

    public int getExitCount() {
        return exitCount;
    }

    public void setExitCount(int exitCount) {
        this.exitCount = exitCount;
    }

    @JsonIgnore
    public String getCimName() {
        return ObjectTypeIdConstant.LABOR_COUNT_BY_MONTH;
    }
}
