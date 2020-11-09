package com.glodon.pcop.cimsvc.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class EntityAddException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public EntityAddException(EnumWrapper.CodeAndMsg code, String msg) {
        super(code, msg);
        this.code = code;
    }

    public EntityAddException(EnumWrapper.CodeAndMsg code) {
        super(code, code.getMsg());
        this.code = code;
    }

    public EntityAddException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05040406, msg);
        this.code = EnumWrapper.CodeAndMsg.E05040406;
    }

    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }
}
