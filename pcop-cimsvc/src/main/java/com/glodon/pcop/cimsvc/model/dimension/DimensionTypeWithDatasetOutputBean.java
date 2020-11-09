package com.glodon.pcop.cimsvc.model.dimension;

import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "字典配置项类型定义输出")
public class DimensionTypeWithDatasetOutputBean {
    @ApiModelProperty(value = "类型名称", required = true)
    private String dimensionTypeName;
    @ApiModelProperty(value = "类型描述", required = true)
    private String dimensionTypeDesc;
    @ApiModelProperty(value = "属性集", required = true)
    private DataSetEntity dataSet;

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

    public DataSetEntity getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetEntity dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public String toString() {
        return "DimensionTypeWithDatasetOutputBean{" +
                "dimensionTypeName='" + dimensionTypeName + '\'' +
                ", dimensionTypeDesc='" + dimensionTypeDesc + '\'' +
                ", dataSet=" + dataSet +
                '}';
    }
}