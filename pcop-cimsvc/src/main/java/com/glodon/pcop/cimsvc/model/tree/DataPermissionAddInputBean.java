package com.glodon.pcop.cimsvc.model.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Map;

@ApiModel(value = "新增节点权限输入")
public class DataPermissionAddInputBean extends DataPermissionBaseBean implements Serializable {
    static final long serialVersionUID = -2875312315119871104L;

    @JsonIgnore
    private String nodeRid;

    @JsonIgnore
    private Map<String, Object> relationProperties;

    public DataPermissionAddInputBean(String nodeRid) {
        this.nodeRid = nodeRid;
        this.setReadPermission(2);
        this.setWritePermission(2);
        this.setDeletePermission(2);
    }

    public DataPermissionAddInputBean() {
    }

    public String getNodeRid() {
        return nodeRid;
    }

    public void setNodeRid(String nodeRid) {
        this.nodeRid = nodeRid;
    }

    public Map<String, Object> getRelationProperties() {
        return relationProperties;
    }

    public void setRelationProperties(Map<String, Object> relationProperties) {
        this.relationProperties = relationProperties;
    }
}
