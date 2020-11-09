package com.glodon.pcop.cimstatsvc.controller;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimstatsvc.model.StatParameter;
import com.glodon.pcop.cimstatsvc.statistic.LaborAttendanceStatistic;
import com.glodon.pcop.cimstatsvc.statistic.LaborOverAllStatistic;
import com.glodon.pcop.cimstatsvc.statistic.ManagerAttendanceStatistic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "/test", tags = "测试统计结果")
@RestController
@Component
public class TestController {

    @ApiOperation(value = "测试劳务统计", notes = "测试劳务统计")
    @GetMapping("/testLaborCount")
    public String testLabor() {
        LaborAttendanceStatistic.count();
        return "";
    }

    @ApiOperation(value = "测试管理人员统计", notes = "测试管理人员统计")
    @GetMapping("/testManagerCount")
    public String testManager() {
        ManagerAttendanceStatistic.count();
        return "";
    }


    @ApiOperation(value = "测试劳务概述统计", notes = "测试劳务概述统计")
    @GetMapping("/testLaborOverAllCount")
    public String testLaborOverAll() {
        LaborOverAllStatistic.count();
        return "";
    }
}
