package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "施工、监理单位")
public class SupervisingUnitBean {

	public SupervisingUnitBean() {
	}

	public SupervisingUnitBean(String id, String unitName, List<WorkerBean> personList) {
		this.id = id;
		this.unitName = unitName;
		this.personList = personList;
	}

	@ApiModelProperty(value = "ID", required = false)
	private String id;

	@ApiModelProperty(value = "单位名称", required = true)
	private String unitName;

	@ApiModelProperty(value = "人员列表", required = true)
	private List<WorkerBean> personList;

	public String getId() {
		return id;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public List<WorkerBean> getPersonList() {
		return personList;
	}

	public void setPersonList(List<WorkerBean> personList) {
		this.personList = personList;
	}
}
