package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "分页查询输出基础类")
public class BasePageableQueryOutput<T> {
    @ApiModelProperty(value = "总量", example = "123")
    private Long totalCount;
    private List<T> instances;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getInstances() {
        return instances;
    }

    public void setInstances(List<T> instances) {
        this.instances = instances;
    }
}
