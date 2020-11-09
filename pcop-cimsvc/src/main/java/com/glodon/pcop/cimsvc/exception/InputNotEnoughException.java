package com.glodon.pcop.cimsvc.exception;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class InputNotEnoughException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public InputNotEnoughException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05040006, msg);
        this.code = EnumWrapper.CodeAndMsg.E05040006;
    }

    @Override
    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }

    public void setCode(EnumWrapper.CodeAndMsg code) {
        this.code = code;
    }
}
