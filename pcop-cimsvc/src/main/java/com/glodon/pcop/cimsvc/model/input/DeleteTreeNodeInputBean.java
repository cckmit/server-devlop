package com.glodon.pcop.cimsvc.model.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "删除树节点输入")
public class DeleteTreeNodeInputBean extends TreeNodeInputBean {
    @JsonIgnore
    @ApiModelProperty(value = "所属行业分类RID")
    private String industryRid;

    public String getIndustryRid() {
        return industryRid;
    }

    public void setIndustryRid(String industryRid) {
        this.industryRid = industryRid;
    }
}


