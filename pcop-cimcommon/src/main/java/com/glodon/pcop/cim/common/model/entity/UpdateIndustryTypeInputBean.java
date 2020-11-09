package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "desc", "parentId"})
public class UpdateIndustryTypeInputBean {
    private String desc;
    private String parentId;

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

    @Override
    public String toString() {
        return "UpdateIndustryTypeInputBean{" +
                "desc='" + desc + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}