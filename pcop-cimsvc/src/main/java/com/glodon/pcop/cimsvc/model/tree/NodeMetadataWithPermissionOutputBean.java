package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "树节点元数据，权限数据查询输出")
public class NodeMetadataWithPermissionOutputBean extends NodeMetadataOutputBean {

    private DataPermissionBean permission;

    public DataPermissionBean getPermission() {
        return permission;
    }

    public void setPermission(DataPermissionBean permission) {
        this.permission = permission;
    }
}


