package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "新增场景树节点输入")
public class SceneTreeNodeAddInputBean {
    private String treeName;
    // private NodeInfoBean parentNodeInfo;
    private Map<String, Object> metadata;

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    // public NodeInfoBean getParentNodeInfo() {
    //     return parentNodeInfo;
    // }
    //
    // public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
    //     this.parentNodeInfo = parentNodeInfo;
    // }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "SceneTreeNodeAddInputBean{" +
                "treeName='" + treeName + '\'' +
                // ", parentNodeInfo=" + parentNodeInfo +
                ", metadata=" + metadata +
                '}';
    }
}
