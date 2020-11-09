package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "挂载对象类型和实例到树节点")
public class LinkObjectTypeAndInstanceInputBean {
    @ApiModelProperty(value = "父节点信息")
    private NodeInfoBean parentNodeInfo;
    @ApiModelProperty(value = "对象类型和实例rid")
    private List<ObjectAndInstanceBean> objectAndInstances;

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public List<ObjectAndInstanceBean> getObjectAndInstances() {
        return objectAndInstances;
    }

    public void setObjectAndInstances(List<ObjectAndInstanceBean> objectAndInstances) {
        this.objectAndInstances = objectAndInstances;
    }

    @Override
    public String toString() {
        return "LinkObjectTypeAndInstanceInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", objectAndInstances=" + objectAndInstances +
                '}';
    }
}
