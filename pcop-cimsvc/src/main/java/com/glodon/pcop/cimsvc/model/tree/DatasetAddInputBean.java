package com.glodon.pcop.cimsvc.model.tree;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 树上生成的
 */
@ApiModel(value = "新增属性集到树节点")
public class DatasetAddInputBean {

    @ApiModelProperty(value = "父节点信息")
    private NodeInfoBean parentNodeInfo;
    @ApiModelProperty(value = "属性集名称")
    private String datasetName;
    @ApiModelProperty(value = "属性集ID")
    private String datasetId;
    @ApiModelProperty(value = "描述")
    private String desc;

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DatasetAddInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", datasetName='" + datasetName + '\'' +
                ", datasetId='" + datasetId + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
