package com.glodon.pcop.cimsvc.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class BimfaceProcessingException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public BimfaceProcessingException(EnumWrapper.CodeAndMsg code, String msg) {
        super(code, msg);
        this.code = code;
    }

    public BimfaceProcessingException(EnumWrapper.CodeAndMsg code) {
        super(code, code.getMsg());
        this.code = code;
    }

    public BimfaceProcessingException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05080002, msg);
        this.code = EnumWrapper.CodeAndMsg.E05080002;
    }

    public BimfaceProcessingException() {
        super(EnumWrapper.CodeAndMsg.E05080002, EnumWrapper.CodeAndMsg.E05080002.getMsg());
        this.code = EnumWrapper.CodeAndMsg.E05080002;
    }

    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }
}
