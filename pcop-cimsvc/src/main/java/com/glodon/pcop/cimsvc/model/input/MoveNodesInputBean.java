package com.glodon.pcop.cimsvc.model.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "移动树节点输入")
public class MoveNodesInputBean {
    @ApiModelProperty(value = "源节点")
    private TreeNodeWithNameInputBean fromNode;
    @ApiModelProperty(value = "目标节点")
    private TreeNodeWithNameInputBean toNode;

    public TreeNodeWithNameInputBean getFromNode() {
        return fromNode;
    }

    public void setFromNode(TreeNodeWithNameInputBean fromNode) {
        this.fromNode = fromNode;
    }

    public TreeNodeWithNameInputBean getToNode() {
        return toNode;
    }

    public void setToNode(TreeNodeWithNameInputBean toNode) {
        this.toNode = toNode;
    }
}
