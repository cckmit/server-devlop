package com.glodon.pcop.cimstatsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cimstatsvc.constant.ObjectTypeIdConstant;

public class ManagerAttendSummaryCountByMonth {
    private String projectId;
    private String projectName;
    private String staticCycle;


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

    @JsonIgnore
    public String getCimName() {
        return ObjectTypeIdConstant.MANAGER_ATTEND_SUMMARY_COUNT_BY_MONTH;
    }
}
