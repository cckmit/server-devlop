package com.glodon.pcop.cimsvc.model.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "单实例")
public class InstanceBaseBean {
    @ApiModelProperty(value = "模型ID")
    private String objectTypeId;
    @ApiModelProperty(value = "实例ID")
    private String id;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "InstanceBaseBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}