package com.glodon.pcop.cimsvc.model.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "树节点查询输入，包含显示名称")
public class TreeNodeWithNameInputBean extends TreeNodeInputBean {
    @ApiModelProperty(value = "显示名称")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


