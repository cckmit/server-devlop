package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "单实例查询输出")
public class SingleInstancesQueryOutput {
    @ApiModelProperty(value = "实例ID")
    private String instanceRid;
    @ApiModelProperty(value = "实例数据")
    private Map<String, Map<String, Object>> instanceData;

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public Map<String, Map<String, Object>> getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(Map<String, Map<String, Object>> instanceData) {
        this.instanceData = instanceData;
    }
}
