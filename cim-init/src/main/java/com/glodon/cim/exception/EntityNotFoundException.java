package com.glodon.cim.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class EntityNotFoundException extends ApiException {
	private EnumWrapper.CodeAndMsg code;

	public EntityNotFoundException(EnumWrapper.CodeAndMsg code, String msg) {
		super(code, msg);
		this.code = code;
	}
	public EntityNotFoundException(EnumWrapper.CodeAndMsg code) {
		super(code, code.getMsg());
		this.code = code;
	}

	public EntityNotFoundException(String msg) {
		super(EnumWrapper.CodeAndMsg.E05040404, msg);
		this.code = EnumWrapper.CodeAndMsg.E05040404;
	}

	public EnumWrapper.CodeAndMsg getCode() {
		return code;
	}
}
