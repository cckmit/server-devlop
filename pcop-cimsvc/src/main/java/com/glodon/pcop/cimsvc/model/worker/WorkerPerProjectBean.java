package com.glodon.pcop.cimsvc.model.worker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(description = "每个项目实时在场人数和在场总数")
public class WorkerPerProjectBean {

	public WorkerPerProjectBean(String projectId, String projectName, String countOnhand, String countIntime) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.countOnhand = countOnhand;
		this.countIntime = countIntime;
	}

	@ApiModelProperty(value = "项目ID", required = true)
	private String projectId;

	@ApiModelProperty(value = "项目名称", required = true)
	private String projectName;

	@ApiModelProperty(value = "在场总数", required = true)
	private String countOnhand;

	@ApiModelProperty(value = "实时在场人数", required = true)
	private String countIntime;


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

	public String getCountOnhand() {
		return countOnhand;
	}

	public void setCountOnhand(String countOnhand) {
		this.countOnhand = countOnhand;
	}

	public String getCountIntime() {
		return countIntime;
	}

	public void setCountIntime(String countIntime) {
		this.countIntime = countIntime;
	}
}
