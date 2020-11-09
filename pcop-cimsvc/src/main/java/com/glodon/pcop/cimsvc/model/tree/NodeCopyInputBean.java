package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "复制树节点输入")
public class NodeCopyInputBean {
    private NodeInfoBean targetNodeInfo;
    private NodeInfoBean nodeInfo;

    public NodeInfoBean getTargetNodeInfo() {
        return targetNodeInfo;
    }

    public void setTargetNodeInfo(NodeInfoBean targetNodeInfo) {
        this.targetNodeInfo = targetNodeInfo;
    }

    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfoBean nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public String toString() {
        return "NodeCopyInputBean{" +
                "targetNodeInfo=" + targetNodeInfo +
                ", nodeInfo=" + nodeInfo +
                '}';
    }
}
