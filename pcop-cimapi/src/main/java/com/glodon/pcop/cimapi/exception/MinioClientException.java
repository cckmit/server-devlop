package com.glodon.pcop.cimapi.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;

public class MinioClientException extends ApiException {
	private EnumWrapper.CodeAndMsg code;

	public MinioClientException(EnumWrapper.CodeAndMsg code, String msg) {
		super(code, msg);
		this.code = code;
	}
	public MinioClientException(EnumWrapper.CodeAndMsg code) {
		super(code, code.getMsg());
		this.code = code;
	}

	public EnumWrapper.CodeAndMsg getCode() {
		return code;
	}
}
