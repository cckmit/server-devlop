package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "属性录入")
public class PropMappingGis {
	@ApiModelProperty(value = "属性ID", required = true)
	private String id;
	@ApiModelProperty(value = "属性名称", required = true)
	private String name;
	@ApiModelProperty(value = "目标属性名称", required = true)
	private String targetName;
	@ApiModelProperty(value = "目标属性值", required = true)
	private String targetValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}

}