package com.glodon.pcop.cimsvc.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "手工录入数据")
public class InputInstancesBean {
	@ApiModelProperty(value = "对象实例list")
	public List<InstanceBean> instances;

	public List<InstanceBean> getInstances() {
		return instances;
	}

	public void setInstances(List<InstanceBean> instances) {
		this.instances = instances;
	}
}


