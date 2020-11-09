package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "移动树节点输入")
public class NodeMoveInputBean {
    private NodeInfoBean beforeNodeInfo;
    private NodeInfoBean nodeInfo;
    private NodeInfoBean afterNodeInfo;
    private NodeInfoBean parentNodeInfo;

    public NodeInfoBean getBeforeNodeInfo() {
        return beforeNodeInfo;
    }

    public void setBeforeNodeInfo(NodeInfoBean beforeNodeInfo) {
        this.beforeNodeInfo = beforeNodeInfo;
    }

    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfoBean nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public NodeInfoBean getAfterNodeInfo() {
        return afterNodeInfo;
    }

    public void setAfterNodeInfo(NodeInfoBean afterNodeInfo) {
        this.afterNodeInfo = afterNodeInfo;
    }

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }


    @Override
    public String toString() {
        return "NodeMoveInputBean{" +
                "beforeNodeInfo=" + beforeNodeInfo +
                ", nodeInfo=" + nodeInfo +
                ", afterNodeInfo=" + afterNodeInfo +
                ", parentNodeInfo=" + parentNodeInfo +
                '}';
    }
}
