package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.IndustryTypeEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.v2.IndustryTypeTreeQueryOutput;
import com.glodon.pcop.cimsvc.service.v2.IndustryTypesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author yuanjk
 * @description 行业分类定义相关接口
 */
@Api(tags = "行业分类")
@RestController
@RequestMapping("/industryTypes")
public class IndustryTypeController {
    static Logger log = LoggerFactory.getLogger(IndustryTypeController.class);

    @Autowired
    private IndustryTypesService industryTypeService;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "新增行业分类", notes = "根据输入的对象新增行业分类模型", response = IndustryTypeEntity.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ReturnInfo addIndustryClass(@Valid @RequestBody AddIndustryTypeInputBean industryType,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws DataServiceModelRuntimeException {
        log.info("addIndustryType(industryType={})", industryType);
        Assert.hasText(tenantId, "tenant should not be blank");
        AddIndustryTypeOutputBean results = industryTypeService.addIndustryType(tenantId, userId, industryType);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "删除行业分类", notes = "根据输入行业分类typeId删除行业分类", response = Boolean.class)
    @RequestMapping(value = "/{industryTypeRid}", method = RequestMethod.DELETE)
    public ReturnInfo deleteIndustryClass(@PathVariable String industryTypeRid,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException {
        log.info("deleteIndustryClass(industryTypeRid={})", industryTypeRid);
        boolean flag = industryTypeService.removeIndustryType(tenantId, industryTypeRid);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, flag);
        return ri;
    }

    @ApiOperation(value = "更新行业分类", notes = "根据输入的typeId和对象更新行业分类", response = IndustryTypeEntity.class)
    @RequestMapping(value = "/{industryTypeRid}", method = RequestMethod.PUT)
    public ReturnInfo updateIndustryClass(@PathVariable String industryTypeRid,
                                          @RequestBody UpdateIndustryTypeInputBean industryType,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws EntityNotFoundException, DataServiceModelRuntimeException {
        log.info("updateIndustryType(industryTypeRid={}, industryType={})", industryTypeRid, industryType);
        Assert.hasText(tenantId, "tenant should not be blank");
        UpdateIndustryTypeOutputBean results = industryTypeService.updateIndustryType(tenantId, userId,
                industryTypeRid, industryType);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "行业分类详情", notes = "根据输入行业分类typeId获取行业分类详情", response = IndustryTypeEntity.class)
    @RequestMapping(value = "/{industryTypeRid}", method = RequestMethod.GET)
    public ReturnInfo getIndustryClass(@PathVariable String industryTypeRid,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("getIndustryType(typeId={})", industryTypeRid);
        IndustryTypeEntity results = industryTypeService.getIndustryType(tenantId, industryTypeRid);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05040003;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "行业分类的子类和对象类型", notes = "行业分类的子类和对象类型", response = IndustryTypeTreeQueryOutput.class)
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public ReturnInfo getIndustryTypeTree(@RequestParam(required = false) String industryTypeRid,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("getIndustryTypeTree(industryTypeRid={})", industryTypeRid);
        Assert.hasText(tenantId, "tenant should not be blank");
        IndustryTypeTreeQueryOutput results =
                industryTypeService.getAllChildIndustryTypesAndLinkedObjectTypes(tenantId, industryTypeRid);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, results);
        return ri;
    }

}
