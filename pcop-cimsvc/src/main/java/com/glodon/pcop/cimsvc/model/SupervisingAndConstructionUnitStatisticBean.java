package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "监理、施工单位统计信息")
public class SupervisingAndConstructionUnitStatisticBean {

	@ApiModelProperty(value = "施工单位统计信息", required = true)
	private List<SupervisingUnitBean> shigong;

	@ApiModelProperty(value = "监理单位统计信息", required = true)
	private List<SupervisingUnitBean> jianli;

	public SupervisingAndConstructionUnitStatisticBean() {
	}

	public SupervisingAndConstructionUnitStatisticBean(List<SupervisingUnitBean> shigong, List<SupervisingUnitBean> jianli) {
		this.shigong = shigong;
		this.jianli = jianli;
	}

	public List<SupervisingUnitBean> getShigong() {
		return shigong;
	}

	public void setShigong(List<SupervisingUnitBean> shigong) {
		this.shigong = shigong;
	}

	public List<SupervisingUnitBean> getJianli() {
		return jianli;
	}

	public void setJianli(List<SupervisingUnitBean> jianli) {
		this.jianli = jianli;
	}
}
