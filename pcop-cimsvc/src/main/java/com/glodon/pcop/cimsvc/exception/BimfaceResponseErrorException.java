package com.glodon.pcop.cimsvc.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class BimfaceResponseErrorException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public BimfaceResponseErrorException(EnumWrapper.CodeAndMsg code, String msg) {
        super(code, msg);
        this.code = code;
    }

    public BimfaceResponseErrorException(EnumWrapper.CodeAndMsg code) {
        super(code, code.getMsg());
        this.code = code;
    }

    public BimfaceResponseErrorException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05080003, msg);
        this.code = EnumWrapper.CodeAndMsg.E05080003;
    }

    public BimfaceResponseErrorException() {
        super(EnumWrapper.CodeAndMsg.E05080003, EnumWrapper.CodeAndMsg.E05080003.getMsg());
        this.code = EnumWrapper.CodeAndMsg.E05080003;
    }

    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }
}
