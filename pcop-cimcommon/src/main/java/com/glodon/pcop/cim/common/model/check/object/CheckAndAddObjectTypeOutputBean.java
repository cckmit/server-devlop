package com.glodon.pcop.cim.common.model.check.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "检查，新增对象模型和属性集返回结果")
public class CheckAndAddObjectTypeOutputBean {
    private String objectName;
    private Boolean isSuccess;

    private String message;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
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

    public CheckAndAddObjectTypeOutputBean() {
    }

    public CheckAndAddObjectTypeOutputBean(String objectName) {
        this.objectName = objectName;
    }
}
