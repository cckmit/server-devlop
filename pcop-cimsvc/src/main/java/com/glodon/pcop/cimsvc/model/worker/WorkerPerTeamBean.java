package com.glodon.pcop.cimsvc.model.worker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "队伍人数")
public class WorkerPerTeamBean {

    public WorkerPerTeamBean(String teamId, String teamName, String count) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.count = count;
    }

    @ApiModelProperty(value = "队伍ID", required = true)
    private String teamId;

    @ApiModelProperty(value = "队伍名称", required = true)
    private String teamName;

    @ApiModelProperty(value = "队伍人数", required = true)
    private String count;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
