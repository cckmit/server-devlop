package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "单实例数据删除输入参数")
public class DeleteByConditionInputBean extends InstancesQueryInput {
    private String property;
    private String relatedObjectTypeId;
    private String relatedProperty;

    public String getRelatedObjectTypeId() {
        return relatedObjectTypeId;
    }

    public void setRelatedObjectTypeId(String relatedObjectTypeId) {
        this.relatedObjectTypeId = relatedObjectTypeId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getRelatedProperty() {
        return relatedProperty;
    }

    public void setRelatedProperty(String relatedProperty) {
        this.relatedProperty = relatedProperty;
    }

    @Override
    public String toString() {
        return "DeleteByConditionInputBean{" +
                "property='" + property + '\'' +
                ", relatedObjectTypeId='" + relatedObjectTypeId + '\'' +
                ", relatedProperty='" + relatedProperty + '\'' +
                "} " + super.toString();
    }
}
