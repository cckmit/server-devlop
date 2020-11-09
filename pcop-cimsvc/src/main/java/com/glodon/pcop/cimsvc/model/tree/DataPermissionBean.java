package com.glodon.pcop.cimsvc.model.tree;

import java.io.Serializable;

public class DataPermissionBean implements Serializable {
    static final long serialVersionUID = 6061759456036547098L;

    private String nodeId;
    private String rid;
    private Integer readPermission;
    private Integer writePermission;
    private Integer deletePermission;

    public void mergeDataPermission(DataPermissionBean dataPermission) {
        if (dataPermission == null) {
            return;
        }
        this.readPermission = Math.max(this.readPermission, dataPermission.getReadPermission());
        this.writePermission = Math.max(this.writePermission, dataPermission.getWritePermission());
        this.deletePermission = Math.max(this.deletePermission, dataPermission.getDeletePermission());
    }

    public DataPermissionBean(String nodeId, String rid, Integer readPermission, Integer writePermission,
                              Integer deletePermission) {
        this.nodeId = nodeId;
        this.rid = rid;
        this.readPermission = readPermission;
        this.writePermission = writePermission;
        this.deletePermission = deletePermission;
    }

    public DataPermissionBean() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
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

    @Override
    public String toString() {
        return "DataPermissionBean{" +
                "nodeId='" + nodeId + '\'' +
                ", rid='" + rid + '\'' +
                ", readPermission=" + readPermission +
                ", writePermission=" + writePermission +
                ", deletePermission=" + deletePermission +
                '}';
    }
}
