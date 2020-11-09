package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "desc", "creator", "disabled", "parentId", "industryTypeId", "linkedDataSets",
        "createTime", "updateTime"})
public class AddObjectTypeInputBean {
    private String name;
    private String desc;

    private String parentId;
    private String industryTypeId;

    @JsonIgnore
    private String tenantId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "AddObjectTypeInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", parentId='" + parentId + '\'' +
                ", industryTypeId='" + industryTypeId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
