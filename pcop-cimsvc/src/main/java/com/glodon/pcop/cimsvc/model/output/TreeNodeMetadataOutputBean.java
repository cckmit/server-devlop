package com.glodon.pcop.cimsvc.model.output;

import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ApiModel(value = "树节点元数据查询输出")
public class TreeNodeMetadataOutputBean extends TreeNodeInputBean {
    @ApiModelProperty(value = "元数据", position = 10)
    private Map<String, Object> metadata;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public static TreeNodeMetadataOutputBean transferFactory(TreeNodeInputBean inputBean) {
        TreeNodeMetadataOutputBean outputBean = new TreeNodeMetadataOutputBean();
        outputBean.setId(inputBean.getId());
        outputBean.setInstanceRid(inputBean.getInstanceRid());
        outputBean.setNodeType(inputBean.getNodeType());
        outputBean.setParentIndustryRid(inputBean.getParentIndustryRid());
        return outputBean;
    }
}


