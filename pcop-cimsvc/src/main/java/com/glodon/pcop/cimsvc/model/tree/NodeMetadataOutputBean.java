package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "节点元数据输出")
public class NodeMetadataOutputBean {

    public NodeMetadataOutputBean() {
    }

    private Map<String, Object> metadata;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
