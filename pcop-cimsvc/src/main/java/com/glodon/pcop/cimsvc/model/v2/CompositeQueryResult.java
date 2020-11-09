package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2019/8/14 20:14
 */
@ApiModel(value = "单实例多源异构融合查询输出结果")
public class CompositeQueryResult {
    @ApiModelProperty(value = "实例ID")
    private String instanceRid;

    @ApiModelProperty(value = "融合的数据结果")
    private List<Map<String,Object>> dataInstancesProperties;


    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public List<Map<String, Object>> getDataInstancesProperties() {
        return dataInstancesProperties;
    }

    public void setDataInstancesProperties(List<Map<String, Object>> dataInstancesProperties) {
        this.dataInstancesProperties = dataInstancesProperties;
    }
}
