package com.glodon.pcop.cimsvc.model.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "删除树节点输出")
public class DeleteTreeNodeOutputBean extends TreeNodeInputBean {
    @JsonIgnore
    @ApiModelProperty(value = "所属行业分类RID")
    private String industryRid;

    @JsonProperty(value = "is_success")
    @ApiModelProperty(value = "是否成功删除标识")
    private boolean success;


    public String getIndustryRid() {
        return industryRid;
    }

    public void setIndustryRid(String industryRid) {
        this.industryRid = industryRid;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}


