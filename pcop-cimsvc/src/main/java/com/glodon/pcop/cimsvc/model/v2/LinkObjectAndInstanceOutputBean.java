package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "挂载对象类型和实例到行业分类输出")
public class LinkObjectAndInstanceOutputBean {
    @ApiModelProperty(value = "对象类型ID")
    private String objectTypeId;

    @ApiModelProperty(value = "对象类型名称")
    private String objectTypeName;

    @ApiModelProperty(value = "挂载是否成功")
    private Boolean isSuccess = true;

    @ApiModelProperty(value = "操作信息")
    private String message = "success";

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

    @JsonProperty(value = "is_success")
    public Boolean getSuccess() {
        return isSuccess;
    }


    @JsonProperty(value = "is_success")
    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
