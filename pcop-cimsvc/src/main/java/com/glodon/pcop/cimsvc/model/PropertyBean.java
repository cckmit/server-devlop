package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "属性")
public class PropertyBean {

	public PropertyBean() {
		super();
	}

	public PropertyBean(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@ApiModelProperty(value = "ID", required = false)
	private String id;

//	@ApiModelProperty(value = "序列号", required = true)
//	private Integer idx;

	@ApiModelProperty(value = "名称", required = true)
	private String name;

	@ApiModelProperty(value = "别名", required = true)
	private String alias;

	@ApiModelProperty(value = "类型ID", required = true)
	private String typeId;

	@ApiModelProperty(value = "类型", required = true)
	private String typeName;

//	@ApiModelProperty(value = "长度", required = false)
//	private Integer length;

	@ApiModelProperty(value = "目标名称")
	private String targetName;

	@ApiModelProperty(value = "导入值")
	private String targetValue;

	@ApiModelProperty(value = "是否为空", required = true)
//	@JsonProperty("is_null")
	private Boolean isNull;


	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getTargetValue() {
		return targetValue;
	}

	public Boolean getIsNull() {
		return isNull;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}

	public void setIsNull(Boolean aNull) {
		isNull = aNull;
	}
}
