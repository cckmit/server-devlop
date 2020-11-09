package com.glodon.pcop.cimsvc.model.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class ObjectBatchBean extends BaseBatchBean {
    private String objectTypeId;
    @ApiModelProperty(value = "分类idx", example = "123")
    private int industryIndex;
    private String industryName;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    @JsonProperty("industry_type_index")
    public int getIndustryIndex() {
        return industryIndex;
    }

    @JsonProperty("industry_type_index")
    public void setIndustryIndex(int industryIndex) {
        this.industryIndex = industryIndex;
    }

    @JsonProperty("industry_type_name")
    public String getIndustryName() {
        return industryName;
    }

    @JsonProperty("industry_type_name")
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }
}
