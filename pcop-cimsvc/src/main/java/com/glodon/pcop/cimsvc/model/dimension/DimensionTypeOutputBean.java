package com.glodon.pcop.cimsvc.model.dimension;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "字典配置项类型定义输出")
public class DimensionTypeOutputBean {
    @ApiModelProperty(value = "类型名称", required = true)
    private String dimensionTypeName;
    @ApiModelProperty(value = "类型描述", required = true)
    private String dimensionTypeDesc;

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

}