package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "desc", "relation_type", "source_object_type_id", "target_object_type_id", "conditions"})
public class RelationshipEntity  {
    private String id;
    private String desc;
    private String relationType;
    private String sourceObjectTypeId;
    private String targetObjectTypeId;
    private List<RelationshipConditionBean> conditions;
    @JsonIgnore
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getSourceObjectTypeId() {
        return sourceObjectTypeId;
    }

    public void setSourceObjectTypeId(String sourceObjectTypeId) {
        this.sourceObjectTypeId = sourceObjectTypeId;
    }

    public String getTargetObjectTypeId() {
        return targetObjectTypeId;
    }

    public void setTargetObjectTypeId(String targetObjectTypeId) {
        this.targetObjectTypeId = targetObjectTypeId;
    }

    public List<RelationshipConditionBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<RelationshipConditionBean> conditions) {
        this.conditions = conditions;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
