package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "name", "desc", "creator","updator", "parentId", "childIds", "linkedObjectTypes", "createTime", "updateTime"})
// @JsonIgnoreProperties({"tenantId"})
public class IndustryTypeEntity extends BaseEntity {
    private String parentId;
    @JsonIgnore
    private List<IndustryTypeEntity> childIds;
    @JsonIgnore
    private List<ObjectTypeEntity> linkedObjectTypes;
    @JsonIgnore
    private String tenantId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<IndustryTypeEntity> getChildIds() {
        return childIds;
    }

    public void setChildIds(List<IndustryTypeEntity> childIds) {
        this.childIds = childIds;
    }

    public List<ObjectTypeEntity> getLinkedObjectTypes() {
        return linkedObjectTypes;
    }

    public void setLinkedObjectTypes(List<ObjectTypeEntity> linkedObjectTypes) {
        this.linkedObjectTypes = linkedObjectTypes;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}