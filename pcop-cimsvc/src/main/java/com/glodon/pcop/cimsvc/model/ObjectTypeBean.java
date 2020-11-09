package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(description = "对象模型")
public class ObjectTypeBean {

	public ObjectTypeBean() {
		super();
	}

	public ObjectTypeBean(String typeId, String typeName) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
	}

	@ApiModelProperty(value = "ID")
	private String typeId;

	@ApiModelProperty(value = "名称", required = true)
	private String typeName;

	@ApiModelProperty(value = "继承对象ID")
	private String parentTypeId;

	@ApiModelProperty(value = "行业分类ID", required = true)
	private String industryTypeId;

//	@ApiModelProperty(value = "示例模型ID", required = false)
//	private String exampleModalId;

	@ApiModelProperty(value = "属性集数据集合")
	public List<PropertySetBean> propertySet = new ArrayList<>();

	@ApiModelProperty(value = "创建时间", required = false)
	private Date createDateTime;

	@ApiModelProperty(value = "更新时间", required = false)
	private Date updateDateTime;

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getParentTypeId() {
		return parentTypeId;
	}

	public String getIndustryTypeId() {
		return industryTypeId;
	}

//	public String getExampleModalId() {
//		return exampleModalId;
//	}

	public List<PropertySetBean> getPropertySet() {
		return propertySet;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setParentTypeId(String parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public void setIndustryTypeId(String industryTypeId) {
		this.industryTypeId = industryTypeId;
	}

//	public void setExampleModalId(String exampleModalId) {
//		this.exampleModalId = exampleModalId;
//	}

	public void setPropertySet(List<PropertySetBean> propertySet) {
		this.propertySet = propertySet;
	}

}
