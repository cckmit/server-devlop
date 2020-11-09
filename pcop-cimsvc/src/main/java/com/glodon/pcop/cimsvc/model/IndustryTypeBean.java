package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author yuanjk(yuanjk@glodon.com), 2018-07-31 09:55:58
 *
 */
@ApiModel(description = "行业分类")
public class IndustryTypeBean {

	@ApiModelProperty(value = "ID")
	private String typeId;

	@ApiModelProperty(value = "行业分类名称")
	private String typeName;

	@ApiModelProperty(value = "父类ID")
	private String parentTypeId;

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

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(String parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

}
