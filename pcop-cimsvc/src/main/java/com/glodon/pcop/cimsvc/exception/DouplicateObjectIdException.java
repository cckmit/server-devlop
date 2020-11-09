package com.glodon.pcop.cimsvc.exception;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.exception.ApiException;

public class DouplicateObjectIdException extends ApiException {
    private EnumWrapper.CodeAndMsg code;

    public DouplicateObjectIdException(String msg) {
        super(EnumWrapper.CodeAndMsg.E05040008, msg);
        this.code = EnumWrapper.CodeAndMsg.E05040008;
    }

    @Override
    public EnumWrapper.CodeAndMsg getCode() {
        return code;
    }

    public void setCode(EnumWrapper.CodeAndMsg code) {
        this.code = code;
    }
}
