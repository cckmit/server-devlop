package com.glodon.pcop.cimsvc.exception;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class InputErrorException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public InputErrorException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05040007, msg);
        this.code = EnumWrapper.CodeAndMsg.E05040007;
    }

    @Override
    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }

    public void setCode(EnumWrapper.CodeAndMsg code) {
        this.code = code;
    }
}
