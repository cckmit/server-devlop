package com.glodon.pcop.cimsvc.model.v2;

import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntityWithoutDataSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "对象模型查询结果")
public class ObjectTypeQueryOutput {
    @ApiModelProperty(value = "总量", example = "4092599349000")
    private Long totalCount;

    @ApiModelProperty(value = "对象列表")
    private List<ObjectTypeEntityWithoutDataSet> objectTypes;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<ObjectTypeEntityWithoutDataSet> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<ObjectTypeEntityWithoutDataSet> objectTypes) {
        this.objectTypes = objectTypes;
    }
}
