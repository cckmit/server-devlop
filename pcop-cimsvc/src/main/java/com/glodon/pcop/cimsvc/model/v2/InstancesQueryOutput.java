package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "实例查询输出")
public class InstancesQueryOutput {
    @ApiModelProperty(value = "实例ID")
    private String instanceRid;
    @ApiModelProperty(value = "实例数据")
    private Map instanceData;

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public Map getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(Map instanceData) {
        this.instanceData = instanceData;
    }
}
