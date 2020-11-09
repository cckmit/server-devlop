package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel(value = "新增节点权限基础")
public class DataPermissionBaseBean implements Serializable {
    static final long serialVersionUID = -5066469212111639885L;
    private String nodeId;
    private Integer readPermission;
    private Integer writePermission;
    private Integer deletePermission;

    public DataPermissionBaseBean() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(Integer readPermission) {
        this.readPermission = readPermission;
    }

    public Integer getWritePermission() {
        return writePermission;
    }

    public void setWritePermission(Integer writePermission) {
        this.writePermission = writePermission;
    }

    public Integer getDeletePermission() {
        return deletePermission;
    }

    public void setDeletePermission(Integer deletePermission) {
        this.deletePermission = deletePermission;
    }
}
