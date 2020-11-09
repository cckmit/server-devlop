package com.glodon.pcop.cimapi;

import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimapi.model.InstanceQueryOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 10:51
 * @Description:
 */
@Api(value = "/", tags = "数据导入--v1")
public interface DataImportApi {

    @ApiOperation(value = "实例数据查询", notes = "跟据输入的条件查询实例数据", response = InstanceQueryOutput.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "conditions", value = "查询条件，绝对时间：startTime#endTime", required = false, dataType = "InstanceQueryInputBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/queryInstance", method = RequestMethod.POST)
    ReturnInfo instanceDataQuery(@RequestBody InstanceQueryInputBean conditions,
                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId);


}
