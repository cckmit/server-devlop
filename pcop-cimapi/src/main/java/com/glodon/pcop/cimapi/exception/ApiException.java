package com.glodon.pcop.cimapi.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;

public class ApiException extends Exception {
	private EnumWrapper.CodeAndMsg code;

	public ApiException(EnumWrapper.CodeAndMsg code, String msg) {
		super(msg);
		this.code = code;
	}

	public EnumWrapper.CodeAndMsg getCode() {
		return code;
	}
}
