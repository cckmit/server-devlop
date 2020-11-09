package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "属性集类型")
public class PropertySetTypeBean {

	public PropertySetTypeBean(String typeId, String typeName) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
	}

	@ApiModelProperty(value = "ID", required = false)
	private String typeId;

	@ApiModelProperty(value = "显示名称", required = true)
	private String typeName;

	public String getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

}
