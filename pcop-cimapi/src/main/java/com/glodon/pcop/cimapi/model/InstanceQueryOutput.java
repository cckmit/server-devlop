package com.glodon.pcop.cimapi.model;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实例查询输出
 */
@ApiModel(description = "实例查询输出")
public class InstanceQueryOutput {
    @ApiModelProperty(value = "属性值", example = "4092599349000")
    private Long totalCount;

    @ApiModelProperty(value = "属性值")
    private List<Map<String, Object>> instances;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<Map<String, Object>> getInstances() {
        return instances;
    }

    public void setInstances(List<Map<String, Object>> instances) {
        this.instances = instances;
    }

}
