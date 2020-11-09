package com.glodon.pcop.cimsvc.config.error;

import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.ApiException;
import com.glodon.pcop.cimapi.exception.ApiRunTimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ReturnInfo> defaultErrorHandler(Exception e) {
        String message;
        CodeAndMsg code;
        if (e instanceof EntityNotFoundException) {
            message = e.getMessage();
            code = ((EntityNotFoundException) e).getCode();
        } else if (e instanceof ApiException) {
            code = ((ApiException) e).getCode();
            message = code.getMsg();
        } else if (e instanceof CimDataEngineDataMartException) {
            e.printStackTrace();
            code = CodeAndMsg.E05010503;
            message = CodeAndMsg.E05010503.getMsg();
        } else if (e instanceof DataServiceModelException) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            if (StringUtils.isNotBlank(msg) && msg.contains("not belongs to tenant")) {
                code = CodeAndMsg.E05040405;
                message = msg;
            } else {
                code = CodeAndMsg.E05010503;
                message = CodeAndMsg.E05010503.getMsg();
            }
        } else if (e instanceof CimDataEngineInfoExploreException) {
            e.printStackTrace();
            code = CodeAndMsg.E05010502;
            message = CodeAndMsg.E05010502.getMsg();
        } else if (e instanceof CimDataEngineRuntimeException) {
            e.printStackTrace();
            code = CodeAndMsg.E05010501;
            message = CodeAndMsg.E05010501.getMsg();
        } else if (e instanceof CimDataEngineException) {
            code = CodeAndMsg.E05010002;
            message = CodeAndMsg.E05010002.getMsg();
        } else if (e instanceof ApiRunTimeException) {
            ApiRunTimeException apiRunTimeException = (ApiRunTimeException) e;
            code = apiRunTimeException.getStatusCode();
            message = apiRunTimeException.getMessage();
        } else {
            e.printStackTrace();
            code = CodeAndMsg.E05010500;
//            message = EnumWrapper.CodeAndMsg.E05010500.getMsg();
            message = e.getMessage();
        }

        // ReturnInfo ri = new ReturnInfo(code, message);
        ReturnInfo ri = new ReturnInfo();
        ri.setCode(code);
        ri.setMessage(message);
        ResponseEntity<ReturnInfo> responseEntity = new ResponseEntity<>(ri, HttpStatus.OK);
        return responseEntity;
    }
}
