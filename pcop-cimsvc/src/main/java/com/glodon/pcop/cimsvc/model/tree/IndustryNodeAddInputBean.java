package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "新增行业分类节点输入")
public class IndustryNodeAddInputBean {
    private NodeInfoBean parentNodeInfo;
    private Map<String, Object> metadata;

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "IndustryNodeAddInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", metadata=" + metadata +
                '}';
    }
}
