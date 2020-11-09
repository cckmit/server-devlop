package com.glodon.pcop.cimapi.common;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

/**
 * @author yuanjk 请求返回模板
 */
@Component
@ApiModel(value = "ReturnInfo", description = "请求返回的内容")
public class ReturnInfo {

    @ApiModelProperty(value = "状态码")
    private CodeAndMsg code;
    @ApiModelProperty(value = "状态码的解释")
    private String message;
    @ApiModelProperty(value = "请求对象")
    private Object data;

    public ReturnInfo(CodeAndMsg code, String msg, Object data) {
        super();
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    // public ReturnInfo(CodeAndMsg code, String message) {
    //     super();
    //     this.code = code;
    //     this.message = message;
    // }

    public ReturnInfo(CodeAndMsg code) {
        super();
        this.code = code;
        this.message = code.getMsg();
    }

    public ReturnInfo(CodeAndMsg code, Object data) {
        super();
        this.code = code;
        this.message = code.getMsg();
        this.data = data;
    }

    public ReturnInfo() {
        super();
    }

    public void setCode(CodeAndMsg code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public CodeAndMsg getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

}
