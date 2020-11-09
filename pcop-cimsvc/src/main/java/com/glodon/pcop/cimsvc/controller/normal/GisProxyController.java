package com.glodon.pcop.cimsvc.controller.normal;

import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.service.GisProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "代理接口")
@RestController
public class GisProxyController {
    private static Logger log = LoggerFactory.getLogger(GisProxyController.class);

    @Autowired
    private GisProxyService gisProxyService;

    @ApiOperation(value = "代理接口", notes = "代理接口", response = FileStructBean.class)
    @RequestMapping(value = "/proxy", method = RequestMethod.POST)
    public Object getFileStructure(@RequestParam(name = "methodType") GisProxyService.HttpMethodTypeEnum methodType,
                                       @RequestParam(name = "methodName") String methodName,
                                       @RequestBody String queryInput,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getFileStructure(methodType={}, methodName={})", methodType, methodName);
        Object ri = gisProxyService.poxyQuery(methodType, methodName, queryInput);
        return ri;
    }


}
