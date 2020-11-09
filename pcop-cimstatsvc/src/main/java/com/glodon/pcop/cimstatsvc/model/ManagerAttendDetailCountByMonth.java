package com.glodon.pcop.cimstatsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;

public class ManagerAttendDetailCountByMonth {
    private String projectId;
    private String projectName;
    private String staticCycle;
    private String personName;    //string	人名
    private String personId;    //string	身份证
    private String duty;    //string	职务
    private int leaveCount;//	int	请假次数
    private double attenceRate;//	double	出勤率
    private int unitType; //	int	单位类型

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStaticCycle() {
        return staticCycle;
    }

    public void setStaticCycle(String staticCycle) {
        this.staticCycle = staticCycle;
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

    public int getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(int leaveCount) {
        this.leaveCount = leaveCount;
    }

    public double getAttenceRate() {
        return attenceRate;
    }

    public void setAttenceRate(double attenceRate) {
        this.attenceRate = attenceRate;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }

    @JsonIgnore
    public String getCimName() {
        return ObjectTypeIdConstant.MANAGER_ATTEND_DETAIL_COUNT_BY_MONTH;
    }
}
