package com.glodon.pcop.cimsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "属性集")
public class DatasetBean {
    @ApiModelProperty(value = "属性集ID")
    private String datasetId;
    @ApiModelProperty(value = "属性集名称", required = true)
    private String datasetName;
    @ApiModelProperty(value = "属性集描述")
    private String datasetDesc;
    @ApiModelProperty(value = "属性集类型", required = true, example = "通用属性集")
    private String datasetClassify;
    @ApiModelProperty(value = "是否为继承")
    @JsonIgnore
    private boolean inheritDataset = false;
    @JsonIgnore
    private String tenantId;

    private List<PropertyTypeVOBean> linkedPropertyTypes;

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetDesc() {
        return datasetDesc;
    }

    public void setDatasetDesc(String datasetDesc) {
        this.datasetDesc = datasetDesc;
    }


    public String getDatasetClassify() {
        return datasetClassify;
    }

    public void setDatasetClassify(String datasetClassify) {
        this.datasetClassify = datasetClassify;
    }

    public boolean isInheritDataset() {
        return inheritDataset;
    }

    public void setInheritDataset(boolean inheritDataset) {
        this.inheritDataset = inheritDataset;
    }

    public List<PropertyTypeVOBean> getLinkedPropertyTypes() {
        return linkedPropertyTypes;
    }

    public void setLinkedPropertyTypes(List<PropertyTypeVOBean> linkedPropertyTypes) {
        this.linkedPropertyTypes = linkedPropertyTypes;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
