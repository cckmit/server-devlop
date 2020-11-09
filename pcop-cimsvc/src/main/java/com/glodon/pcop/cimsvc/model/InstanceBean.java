package com.glodon.pcop.cimsvc.model;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "对象实例")
public class InstanceBean {
	@ApiModelProperty(value = "对象模型ID")
	public String objectTypeId;

	@ApiModelProperty(value = "属性值")
	public Map<String, Object> propertyValue;

	public String getObjectTypeId() {
		return objectTypeId;
	}

	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	public Map<String, Object> getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(Map<String, Object> propertyValue) {
		this.propertyValue = propertyValue;
	}

}