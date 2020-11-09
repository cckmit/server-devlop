package com.glodon.pcop.cimsvc.model.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "根据标签查询实例输出")
public class InstancesByTagOutputBean {
    @ApiModelProperty(value = "对象类型ID")
    private String objectTypeId;
    @ApiModelProperty(value = "实例ID")
    private String instanceRid;
    @ApiModelProperty(value = "基本属性")
    private Map<String, Object> baseInfo;

    public InstancesByTagOutputBean(String objectTypeId, String instanceRid,
                                    Map<String, Object> baseInfo) {
        this.objectTypeId = objectTypeId;
        this.instanceRid = instanceRid;
        this.baseInfo = baseInfo;
    }

    public InstancesByTagOutputBean() {
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public Map<String, Object> getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(Map<String, Object> baseInfo) {
        this.baseInfo = baseInfo;
    }
}
