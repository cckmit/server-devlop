package com.glodon.pcop.spacialimportsvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.EnumWrapper;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
	public ResponseEntity<ReturnInfo> defaultErrorHandler(Exception e) {
		String message = "";
		EnumWrapper.CodeAndMsg code;
		e.printStackTrace();
		code = EnumWrapper.CodeAndMsg.E17020500;
		message = EnumWrapper.CodeAndMsg.E17020500.getMsg();
		ReturnInfo ri = new ReturnInfo(code, message);
		
		ResponseEntity<ReturnInfo> responseEntity = new ResponseEntity<>(ri, HttpStatus.OK);
		return responseEntity;
	}
}
