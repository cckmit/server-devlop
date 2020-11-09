package com.glodon.pcop.cimsvc.model.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "移动树节点输出")
public class MoveNodeOutputBean {
    @ApiModelProperty(value = "移动是否成功")
    private Boolean success = false;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
