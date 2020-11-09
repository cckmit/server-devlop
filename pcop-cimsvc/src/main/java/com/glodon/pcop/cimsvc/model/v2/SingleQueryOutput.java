package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SingleQueryOutput {
    @ApiModelProperty(value = "totalCount", example = "4092599349000")
    private Long totalCount;
    private List<SingleInstancesQueryOutput> instances;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<SingleInstancesQueryOutput> getInstances() {
        return instances;
    }

    public void setInstances(List<SingleInstancesQueryOutput> instances) {
        this.instances = instances;
    }
}
