package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "新增对象类型到树节点")
public class ObjectTypeAddInputBean {
    @ApiModelProperty(value = "父节点信息")
    private NodeInfoBean parentNodeInfo;
    @ApiModelProperty(value = "对象类型名称")
    private String objectTypeName;
    @ApiModelProperty(value = "对象类型ID")
    private String objectTypeId;
    @ApiModelProperty(value = "描述")
    private String desc;


    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ObjectTypeAddInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", ObjectTypeName='" + objectTypeName + '\'' +
                ", ObjectTypeId='" + objectTypeId + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
