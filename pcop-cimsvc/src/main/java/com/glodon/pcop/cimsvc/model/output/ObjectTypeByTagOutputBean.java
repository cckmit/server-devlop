package com.glodon.pcop.cimsvc.model.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.glodon.pcop.cim.common.model.entity.BaseEntity;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;

import java.util.List;

@JsonPropertyOrder({"id", "name", "desc", "creator", "updator", "disabled", "parentId", "industryTypeId", "linkedDataSets", "createTime", "updateTime"})
// @JsonIgnoreProperties({"tenantId"})
public class ObjectTypeByTagOutputBean extends BaseEntity {
	private boolean disabled = false;
	private String parentId;
	private String industryTypeId;
	private List<DataSetEntity> linkedDataSets;

	@JsonIgnore
	private String tenantId;

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getIndustryTypeId() {
		return industryTypeId;
	}

	public void setIndustryTypeId(String industryTypeId) {
		this.industryTypeId = industryTypeId;
	}

	@JsonProperty("data_sets")
	public List<DataSetEntity> getLinkedDataSets() {
		return linkedDataSets;
	}

	@JsonProperty("data_sets")
	public void setLinkedDataSets(List<DataSetEntity> linkedDataSets) {
		this.linkedDataSets = linkedDataSets;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
