package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "对象模型查询结果")
public class ObjectQueryOutput {
    @ApiModelProperty(value = "总量", example = "4092599349000")
    private Long totalCount;

    @ApiModelProperty(value = "对象列表")
    private List<ObjectTypeBean> objects;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<ObjectTypeBean> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectTypeBean> objects) {
        this.objects = objects;
    }
}
