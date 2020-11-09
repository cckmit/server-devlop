package com.glodon.pcop.cim.common.model.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "新增标签输入", parent = CommonTagBaseInputBean.class)
public class CommonTagAddInputBean extends CommonTagBaseInputBean {
    @ApiModelProperty(value = "上级标签")
    private CommonTagOutputBean parentTag;

    public CommonTagOutputBean getParentTag() {
        return parentTag;
    }

    public void setParentTag(CommonTagOutputBean parentTag) {
        this.parentTag = parentTag;
    }

    @Override
    public String toString() {
        return "CommonTagAddInputBean{" +
                "parentTag=" + parentTag +
                "} " + super.toString();
    }
}
