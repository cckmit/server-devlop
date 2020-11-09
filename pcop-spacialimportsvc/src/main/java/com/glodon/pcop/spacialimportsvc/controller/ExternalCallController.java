package com.glodon.pcop.spacialimportsvc.controller;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.spacialimportsvc.service.BimfaceCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
@Api("外部调用")
@RestController
@RequestMapping("/external")
public class ExternalCallController {
    static Logger log = LoggerFactory.getLogger(ExternalCallController.class);

    @Autowired
    private BimfaceCallbackService bimfaceCallbackService;

    @ApiOperation(value = "文件转换回调", notes = "bimface 文件转换完成后的回调接口", response = Boolean.class)
    @RequestMapping(value = "/bimface/translateCallback", method = RequestMethod.GET)
    public ReturnInfo translateCallback(@RequestParam("tenantId") String tenantId,
                                        @RequestParam("objectType") String objectType,
                                        @RequestParam("taskId") String taskId,
                                        @RequestParam("fileId") Long fileId) {
        log.info("translateCallback(tenantId={}, objectType={}, taskId={}, fileId={})", tenantId, objectType, taskId,
                fileId);
        CodeAndMsg code = CodeAndMsg.E17000200;
        boolean data = bimfaceCallbackService.bimfaceCallback(tenantId, objectType, taskId, fileId);
        ReturnInfo ri = new ReturnInfo(code, code.getMsg(), data);
        return ri;
    }

}
