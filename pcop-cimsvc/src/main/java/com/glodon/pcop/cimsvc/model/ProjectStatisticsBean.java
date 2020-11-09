package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "管理人员")
public class ProjectStatisticsBean {
	@ApiModelProperty(value = "项目ID", required = false)
	private String projectId;

	@ApiModelProperty(value = "项目名称", required = true)
	private String projectName;

	@ApiModelProperty(value = "考勤周期", required = true)
	private String attenceCycle;

	public ProjectStatisticsBean() {
	}

	public ProjectStatisticsBean(String projectId, String projectName, String attenceCycle) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.attenceCycle = attenceCycle;
	}

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

	public String getAttenceCycle() {
		return attenceCycle;
	}

	public void setAttenceCycle(String attenceCycle) {
		this.attenceCycle = attenceCycle;
	}
}
