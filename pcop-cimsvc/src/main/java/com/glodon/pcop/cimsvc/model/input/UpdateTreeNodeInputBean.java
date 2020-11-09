package com.glodon.pcop.cimsvc.model.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "更新树节点输入")
public class UpdateTreeNodeInputBean extends TreeNodeInputBean {
    @ApiModelProperty(value = "元数据")
    private Map<String, Object> metaData;

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }
}


