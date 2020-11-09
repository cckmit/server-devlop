package com.glodon.pcop.cimsvc.controller;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.service.BusinessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2019/6/12 14:10
 */

@Api(tags = "资源统计")
@RestController
@RequestMapping(value = "/resourceStatistics")
public class ResourceStatisticsController {
    private static final Logger log = LoggerFactory.getLogger(ResourceStatisticsController.class);

    @Autowired
    private BusinessTypeService businessTypeService;

    @ApiOperation(value = "按业务类型查询", notes = "查询资源统计下按业务类型分类的实例数量", response = String.class, responseContainer = "list")
    @RequestMapping(value = "/businessType", method = RequestMethod.POST)
    public ReturnInfo businessType(@RequestBody List<String> BusinessTypeIds,
        @RequestHeader(name = "PCOP-USERID") String creator,
        @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws InterruptedException {
        Long stDate = System.currentTimeMillis();
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();

        Map<String, Long> data = businessTypeService.countByBusinessType(tenantId,BusinessTypeIds);

        log.info("used time: {}", (System.currentTimeMillis() - stDate));
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "按数据类型查询", notes = "查询资源统计下按数据类型分类的实例数量", response = String.class, responseContainer = "list")
    @RequestMapping(value = "/dataType", method = RequestMethod.POST)
    public ReturnInfo dataType(@RequestHeader(name = "PCOP-USERID") String creator,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws InterruptedException {
        Long stDate = System.currentTimeMillis();
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();

        Map<String, Object> data = businessTypeService.countByDataType(tenantId);

        log.info("used time: {}", (System.currentTimeMillis() - stDate));
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

}
