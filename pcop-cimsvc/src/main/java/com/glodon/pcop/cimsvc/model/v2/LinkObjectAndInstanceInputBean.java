package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "挂载对象类型和实例到行业分类输入")
public class LinkObjectAndInstanceInputBean {
    @ApiModelProperty(value = "对象类型ID")
    private String objectTypeId;
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
}
