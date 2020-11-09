package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "属性类型")
public class PropertyTypeBean {

	public PropertyTypeBean(String typeId, String typeName, String typeValue) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
		this.typeValue = typeValue;
	}

	@ApiModelProperty(value = "ID", required = false)
	private String typeId;

	@ApiModelProperty(value = "显示名称", required = true)
	private String typeName;

	@ApiModelProperty(value = "类型名称", required = true)
	private String typeValue;

	@ApiModelProperty(value = "是否可为空", required = true)
	private Boolean isNull;

	public String getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public Boolean getNull() {
		return isNull;
	}

	public void setNull(Boolean aNull) {
		isNull = aNull;
	}
}
