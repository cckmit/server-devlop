package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "集合查询输出")
public class CollectionQueryOutput {
    @ApiModelProperty(value = "总量", example = "4092599349000")
    private Long totalCount;

    private List<CollectionInstancesQueryOutput> instances;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<CollectionInstancesQueryOutput> getInstances() {
        return instances;
    }

    public void setInstances(List<CollectionInstancesQueryOutput> instances) {
        this.instances = instances;
    }
}
