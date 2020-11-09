package com.glodon.pcop.cimsvc.controller.stat;


import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatInputBean;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimsvc.service.stat.MultiplePropertiesStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "统计功能")
@RestController
@RequestMapping(value = "/stat")

public class MultiplePropertiesStatController {
    private static final Logger log = LoggerFactory.getLogger(MultiplePropertiesStatController.class);

    @Autowired
    private MultiplePropertiesStatService statService;


    @ApiOperation(value = "多属性统计", notes = "多属性统计", response = MultiplePropertiesStatOutputBean.class,
            responseContainer = "list")
    @RequestMapping(value = "/multipleProperties", method = RequestMethod.POST)
    public ReturnInfo multiplePropertiesStat(@RequestBody @Validated MultiplePropertiesStatInputBean statInput,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("multipleProperties(statInput={})", statInput);
        List<MultiplePropertiesStatOutputBean> data = statService.multiplePropertyStat(tenantId, statInput);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

}
