package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonPropertyOrder({"name", "desc", "parentId"})
public class AddIndustryTypeInputBean {
    @NotBlank
    @Size(min = 1, max = 128)
    private String name;
    @NotBlank
    @Size(min = 1, max = 256)
    private String desc;
    private String parentId;

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

    @Override
    public String toString() {
        return "AddIndustryTypeInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}