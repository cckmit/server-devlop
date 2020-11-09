package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "集合实例查询输出")
public class CollectionInstancesQueryOutput {
    @ApiModelProperty(value = "实例ID")
    private String instanceRid;
    @ApiModelProperty(value = "实例数据")
    private Map<String, Object> instanceData;

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public Map<String, Object> getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(Map<String, Object> instanceData) {
        this.instanceData = instanceData;
    }
}
