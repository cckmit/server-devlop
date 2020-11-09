package com.glodon.pcop.cimsvc.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yuanjk(yuanjk@glodon.com), 2018-07-18 08:35:49 行业分类树
 */
@ApiModel(description = "行业分类树")
public class IndustryTypeTreeBean {

	public IndustryTypeTreeBean() {
		super();
	}

	public IndustryTypeTreeBean(IndustryTypeBean parentIndustryType) {
		super();
		this.parentIndustryType = parentIndustryType;
	}

	@ApiModelProperty(value = "行业分类节点")
	public IndustryTypeBean parentIndustryType;

	@ApiModelProperty(value = "对象子节点数组")
	public List<ObjectTypeBean> objectList = new ArrayList<>();

	@ApiModelProperty(value = "行业分类子节点数组")
	public List<IndustryTypeTreeBean> childIndustryList = new ArrayList<>();

	public IndustryTypeBean getParentIndustryType() {
		return parentIndustryType;
	}

	public void setParentIndustryType(IndustryTypeBean parentIndustryType) {
		this.parentIndustryType = parentIndustryType;
	}

	public void setChildIndustryList(List<IndustryTypeTreeBean> childIndustryList) {
		this.childIndustryList = childIndustryList;
	}

	public List<ObjectTypeBean> getObjectList() {
		return objectList;
	}

	public List<IndustryTypeTreeBean> getChildIndustryList() {
		return childIndustryList;
	}

	public void setObjectList(List<ObjectTypeBean> objectList) {
		this.objectList = objectList;
	}

}
