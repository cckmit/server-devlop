package com.glodon.pcop.cim.common.model.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

@ApiModel(description = "新增标签输入")
public class CommonTagBaseInputBean {
    @ApiModelProperty(value = "标签标识：唯一")
    @Size(min = 1)
    private String tagName;
    @ApiModelProperty(value = "标签别名")
    private String tagDesc;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public void setTagDesc(String tagDesc) {
        this.tagDesc = tagDesc;
    }

    @Override
    public String toString() {
        return "CommonTagUpdateInputBean{" +
                "tagName='" + tagName + '\'' +
                ", tagDesc='" + tagDesc + '\'' +
                '}';
    }
}
