package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "树节点删除输出")
public class NodeDeleteOutputBean {
    @ApiModelProperty(value = "节点ID")
    private String ID;
    @ApiModelProperty(value = "是否删除成功")
    private Boolean delete;

    public NodeDeleteOutputBean(String ID, Boolean delete) {
        this.ID = ID;
        this.delete = delete;
    }

    public NodeDeleteOutputBean() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "NodeDeleteOutputBean{" +
                "ID='" + ID + '\'' +
                ", delete=" + delete +
                '}';
    }
}
