package com.glodon.pcop.cimsvc.model.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "树节点查询输入")
public class TreeNodeInputBean {
    @ApiModelProperty(value = "标识")
    private String id;
    @ApiModelProperty(value = "实例ID", position = 1)
    private String instanceRid;
    @ApiModelProperty(value = "节点类型", position = 2)
    private TreeNodeTypeEnum nodeType;
    @ApiModelProperty(value = "所属分类rid", position = 12)
    private String parentIndustryRid;

    public TreeNodeInputBean(String id, String instanceRid, TreeNodeTypeEnum nodeType, String parentIndustryRid) {
        this.id = id;
        this.instanceRid = instanceRid;
        this.nodeType = nodeType;
        this.parentIndustryRid = parentIndustryRid;
    }

    public TreeNodeInputBean() {
    }

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TreeNodeTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(TreeNodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public String getParentIndustryRid() {
        return parentIndustryRid;
    }

    public void setParentIndustryRid(String parentIndustryRid) {
        this.parentIndustryRid = parentIndustryRid;
    }

    public enum TreeNodeTypeEnum {
        INDUSTRY, OBJECT, FILE, INSTANCE;
    }

}


