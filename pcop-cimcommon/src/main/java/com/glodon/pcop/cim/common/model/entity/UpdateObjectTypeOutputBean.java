package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"name", "desc", "creator", "disabled", "parentId", "industryTypeId", "linkedDataSets",
        "createTime", "updateTime"})
public class UpdateObjectTypeOutputBean {
    private String id;
    private String name;
    private String desc;
    private String creator;
    private Date createTime;
    private Date updateTime;

    private boolean disabled = false;
    private String parentId;
    private String industryTypeId;

    @JsonIgnore
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

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
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", disabled=" + disabled +
                ", parentId='" + parentId + '\'' +
                ", industryTypeId='" + industryTypeId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
