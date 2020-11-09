package com.glodon.pcop.cimapi.exception;


import com.glodon.pcop.cim.common.util.EnumWrapper;

public class ApiRunTimeException extends RuntimeException {
    private EnumWrapper.CodeAndMsg statusCode;

    public ApiRunTimeException(EnumWrapper.CodeAndMsg statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public ApiRunTimeException(EnumWrapper.CodeAndMsg statusCode, String msg, Throwable throwable) {
        super(msg, throwable);
        this.statusCode = statusCode;
    }

    public ApiRunTimeException(EnumWrapper.CodeAndMsg statusCode, Throwable throwable) {
        super(statusCode.getMsg(), throwable);
        this.statusCode = statusCode;
    }

    public EnumWrapper.CodeAndMsg getStatusCode() {
        return statusCode;
    }
}
