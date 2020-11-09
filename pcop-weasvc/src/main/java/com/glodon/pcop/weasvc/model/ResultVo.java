package com.glodon.pcop.weasvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "ResultVo", description = "请求返回的内容")
public class ResultVo {

    @ApiModelProperty(value = "状态码")
    private String code;
    @ApiModelProperty(value = "状态码的解释")
    private String message;
    @ApiModelProperty(value = "请求对象")
    private Object data;

    public ResultVo(String code, String msg, Object data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public ResultVo(String code, String msg) {
        this.code = code;
        this.message = msg;
    }
    public ResultVo() {
    }
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }
}


