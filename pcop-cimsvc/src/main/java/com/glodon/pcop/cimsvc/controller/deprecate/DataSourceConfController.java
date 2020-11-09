package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-26 17:20:51
 */
// @Api(tags = "其他服务")
// @RestController
@RequestMapping(path = "/abort")
public class DataSourceConfController {
    static Logger log = LoggerFactory.getLogger(DataSourceConfController.class);

    @ApiOperation(value = "所有数据源", notes = "获取已配置的所有数据源", response = String.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
					"header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
					paramType = "header")})
    @RequestMapping(value = "/dataSourceConf", method = RequestMethod.GET)
    public ReturnInfo getAllDataSourceConf(@RequestHeader(name = "PCOP-USERID") String creator,
                                           @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getAllDataSourceConf()");

        String[] confs = {"本地文件", "CIM数据库", "XX连接数据库", "XX服务器文件夹"};

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), confs);
        return ri;
    }

}
