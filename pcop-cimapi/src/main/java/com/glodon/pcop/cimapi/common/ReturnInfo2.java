package com.glodon.pcop.cimapi.common;

import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yuanjk 请求返回模板
 *
 */
@Component
@ApiModel(value="ReturnInfo", description="请求返回的内容")
public class ReturnInfo2 {

	@ApiModelProperty(value = "状态码")
	private String code;
	@ApiModelProperty(value = "状态码的解释")
	private String message;
	@ApiModelProperty(value = "请求对象")
	private Object data;

	public ReturnInfo2(String code, String msg, Object data) {
		super();
		this.code = code;
		this.message = msg;
		this.data = data;
	}

	public ReturnInfo2(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public ReturnInfo2() {
		super();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Object getData() {
		return data;
	}

	public static void main(String[] args) {
	}

}
