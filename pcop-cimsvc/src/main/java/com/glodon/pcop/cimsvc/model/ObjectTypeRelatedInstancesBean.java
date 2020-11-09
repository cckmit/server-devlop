package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ObjectTypeRelatedInstancesBean extends RelatedInstancesBean {
    @ApiModelProperty(name = "instanceCount", example = "123")
    private int instanceCount;
    private List<RelatedInstancesBean> relatedInstances;

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public List<RelatedInstancesBean> getRelatedInstances() {
        return relatedInstances;
    }

    public void setRelatedInstances(List<RelatedInstancesBean> relatedInstances) {
        this.relatedInstances = relatedInstances;
    }
}
