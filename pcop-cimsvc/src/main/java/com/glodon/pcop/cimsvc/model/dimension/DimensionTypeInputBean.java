package com.glodon.pcop.cimsvc.model.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cimsvc.model.DatasetBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "字典配置项类型定义输入")
public class DimensionTypeInputBean {

    @ApiModelProperty(value = "类型名称", required = true)
    private String dimensionTypeName;
    @ApiModelProperty(value = "类型描述", required = true)
    private String dimensionTypeDesc;
    @JsonIgnore
    private String tenantId;
    @ApiModelProperty(value = "属性集", required = true)
    private DatasetBean linkedDataset;

    public String getDimensionTypeName() {
        return dimensionTypeName;
    }

    public void setDimensionTypeName(String dimensionTypeName) {
        this.dimensionTypeName = dimensionTypeName;
    }

    public String getDimensionTypeDesc() {
        return dimensionTypeDesc;
    }

    public void setDimensionTypeDesc(String dimensionTypeDesc) {
        this.dimensionTypeDesc = dimensionTypeDesc;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public DatasetBean getLinkedDataset() {
        return linkedDataset;
    }

    public void setLinkedDataset(DatasetBean linkedDataset) {
        this.linkedDataset = linkedDataset;
    }
}