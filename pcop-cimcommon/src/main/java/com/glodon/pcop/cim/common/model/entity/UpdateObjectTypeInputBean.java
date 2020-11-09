package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"desc","industryTypeId"})
public class UpdateObjectTypeInputBean {
    private String desc;
    private String industryTypeId;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    public void setIndustryTypeId(String industryTypeId) {
        this.industryTypeId = industryTypeId;
    }

    @Override
    public String toString() {
        return "UpdateObjectTypeInputBean{" +
                "desc='" + desc + '\'' +
                ", industryTypeId='" + industryTypeId + '\'' +
                '}';
    }
}
