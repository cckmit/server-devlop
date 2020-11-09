package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "节点元数据更新输入")
public class NodeMetadataUpdateInputBean {
    private NodeInfoBean nodeInfo;
    private Map<String, Object> metadata;

    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfoBean nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "NodeMetadataUpdateInputBean{" +
                "nodeInfoBean=" + nodeInfo +
                ", metadata=" + metadata +
                '}';
    }
}
