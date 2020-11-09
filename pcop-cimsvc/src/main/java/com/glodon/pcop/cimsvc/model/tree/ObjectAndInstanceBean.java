package com.glodon.pcop.cimsvc.model.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "象类型和实例")
public class ObjectAndInstanceBean {
    @ApiModelProperty(value = "对象类型ID")
    private String objectTypeId;
    @JsonIgnore
    @ApiModelProperty(value = "对象类型名称")
    private String objectTypeName;
    @ApiModelProperty(value = "实例RIDs")
    private List<String> instanceRids;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public List<String> getInstanceRids() {
        return instanceRids;
    }

    public void setInstanceRids(List<String> instanceRids) {
        this.instanceRids = instanceRids;
    }

    @Override
    public String toString() {
        return "ObjectAndInstanceBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", objectTypeName='" + objectTypeName + '\'' +
                ", instanceRids=" + instanceRids +
                '}';
    }
}
