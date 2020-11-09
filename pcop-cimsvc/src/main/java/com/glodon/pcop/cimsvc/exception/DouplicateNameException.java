package com.glodon.pcop.cimsvc.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class DouplicateNameException extends ApiException {
	private EnumWrapper.CodeAndMsg code;

	public DouplicateNameException(EnumWrapper.CodeAndMsg code, String msg) {
		super(code, msg);
		this.code = code;
	}
	public DouplicateNameException(EnumWrapper.CodeAndMsg code) {
		super(code, code.getMsg());
		this.code = code;
	}

	public DouplicateNameException(String msg) {
		super(EnumWrapper.CodeAndMsg.E05040404, msg);
		this.code = EnumWrapper.CodeAndMsg.E05040404;
	}

	public EnumWrapper.CodeAndMsg getCode() {
		return code;
	}
}
