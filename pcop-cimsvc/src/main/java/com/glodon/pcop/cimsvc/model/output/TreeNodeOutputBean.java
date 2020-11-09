package com.glodon.pcop.cimsvc.model.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(value = "树节点查询输出")
public class TreeNodeOutputBean {
    @ApiModelProperty(value = "标识")
    private String id;
    @ApiModelProperty(value = "实例ID", position = 1)
    private String instanceRid;
    @ApiModelProperty(value = "显示名称", position = 2)
    private String name;
    @ApiModelProperty(value = "创建人", position = 3)
    private String creator;
    @ApiModelProperty(value = "最后更新人", position = 4)
    private String updator;
    @ApiModelProperty(value = "备注", position = 5)
    private String comment;
    @ApiModelProperty(value = "节点类型", position = 6)
    private TreeNodeTypeEnum nodeType;
    @ApiModelProperty(value = "数据类型", position = 7)
    private String dataType;
    @ApiModelProperty(value = "源文件名称", position = 8)
    private String srcFileName;
    @ApiModelProperty(value = "已挂载的实例", position = 9)
    private List<TreeNodeOutputBean> linkedInstances;
    @ApiModelProperty(value = "创建时间", position = 10)
    private Date createTime;
    @ApiModelProperty(value = "更新时间", position = 11)
    private Date updateTime;
    @ApiModelProperty(value = "所属分类rid", position = 12)
    private String parentIndustryRid;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public TreeNodeTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(TreeNodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<TreeNodeOutputBean> getLinkedInstances() {
        return linkedInstances;
    }

    public void setLinkedInstances(List<TreeNodeOutputBean> linkedInstances) {
        this.linkedInstances = linkedInstances;
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

    @Override
    public String toString() {
        return "TreeNodeOutputBean{" +
                "id='" + id + '\'' +
                ", instanceRid='" + instanceRid + '\'' +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", updator='" + updator + '\'' +
                ", comment='" + comment + '\'' +
                ", nodeType=" + nodeType +
                ", dataType='" + dataType + '\'' +
                ", srcFileName='" + srcFileName + '\'' +
                ", linkedInstances=" + linkedInstances +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", parentIndustryRid='" + parentIndustryRid + '\'' +
                '}';
    }
}


