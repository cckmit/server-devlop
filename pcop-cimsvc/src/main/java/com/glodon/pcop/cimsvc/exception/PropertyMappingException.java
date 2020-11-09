package com.glodon.pcop.cimsvc.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class PropertyMappingException extends ApiException {
	private EnumWrapper.CodeAndMsg code;

	public PropertyMappingException(EnumWrapper.CodeAndMsg code, String msg) {
		super(code, msg);
		this.code = code;
	}
	public PropertyMappingException(EnumWrapper.CodeAndMsg code) {
		super(code, code.getMsg());
		this.code = code;
	}

}
