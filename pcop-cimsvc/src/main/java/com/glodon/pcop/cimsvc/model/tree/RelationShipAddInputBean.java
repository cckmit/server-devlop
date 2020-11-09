package com.glodon.pcop.cimsvc.model.tree;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 树上生成的
 */
@ApiModel(value = "新增关联关系到树节点")
public class RelationShipAddInputBean {

    @ApiModelProperty(value = "父节点信息")
    private NodeInfoBean parentNodeInfo;
    @ApiModelProperty(value = "关联关系名称")
    private String relationShipName;
    @ApiModelProperty(value = "关联关系ID")
    private String relationShipId;
    @ApiModelProperty(value = "描述")
    private String desc;

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public String getRelationShipName() {
        return relationShipName;
    }

    public void setRelationShipName(String relationShipName) {
        this.relationShipName = relationShipName;
    }

    public String getRelationShipId() {
        return relationShipId;
    }

    public void setRelationShipId(String relationShipId) {
        this.relationShipId = relationShipId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RelationShipAddInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", relationShipName='" + relationShipName + '\'' +
                ", relationShipId='" + relationShipId + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
