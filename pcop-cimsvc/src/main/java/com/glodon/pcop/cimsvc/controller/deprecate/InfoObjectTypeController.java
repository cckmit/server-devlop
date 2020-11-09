package com.glodon.pcop.cimsvc.controller.deprecate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.ObjectQueryOutput;
import com.glodon.pcop.cimsvc.model.ObjectTypeBean;
import com.glodon.pcop.cimsvc.service.ObjectTypeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
// @Api(tags = "v1--对象模型")
// @RestController
@RequestMapping("/abort")
public class InfoObjectTypeController {
    static Logger log = LoggerFactory.getLogger(InfoObjectTypeController.class);

    @Autowired
    private ObjectTypeService objService;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "新增对象模型", notes = "根据输入的对象模型定义新增一个对象模型，及其相关的属性集和属性", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectType", value = "对象模型实体", required = true, dataType = "ObjectTypeBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/objectType", method = RequestMethod.POST)
    public ReturnInfo addObjectType(@RequestBody ObjectTypeBean objectType,
                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("addObjectType(typeName={})", objectType.getTypeName());
        ObjectTypeBean rd = objService.addObjectType(objectType, creator);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), rd);
        return ri;
    }

    @ApiOperation(value = "更新对象模型", notes = "更新对象，属性集，属性", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectTypeId", value = "对象模型ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "objectType", value = "对象模型实体", required = true, dataType = "ObjectTypeBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/objectType/{objectTypeId}", method = RequestMethod.PUT)
    public ReturnInfo updateObjectType(@PathVariable String objectTypeId, @RequestBody ObjectTypeBean objectType,
                                       @RequestHeader(name = "PCOP-USERID") String creator,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws EntityNotFoundException {
        log.info("addObjectType(typeName={}, objectTypeID={})", objectType.getTypeName(), objectTypeId);
        ObjectTypeBean objectTypeBean = objService.updateObjectType(objectTypeId, objectType, creator);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), objectTypeBean);
        return ri;
    }

    @ApiOperation(value = "删除对象模型", notes = "删除对象，属性集，属性", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectTypeId", value = "对象模型ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/objectType/{objectTypeId}", method = RequestMethod.DELETE)
    public ReturnInfo deleteObjectType(@PathVariable String objectTypeId,
                                       @RequestHeader(name = "PCOP-USERID") String creator,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("addObjectType(objectTypeId={})", objectTypeId);
        Boolean flag = objService.deleteObjectType(objectTypeId);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), flag);
        return ri;
    }

    @ApiOperation(value = "继承对象", notes = "获取所有已定义的可用对象", response = ObjectTypeBean.class, responseContainer = "List", hidden = true)
    @RequestMapping(value = "/objectType", method = RequestMethod.GET)
    public ReturnInfo getAllObjectType(@RequestHeader(name = "PCOP-USERID") String creator,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getAllObjectType()");
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(),
                objService.getAllObjectType(""));
        return ri;
    }

    @ApiOperation(value = "对象列表", notes = "根据输入的关键词，在对象名称中检索符合的对象，若关键词为空，则输出所有对象", response = ObjectTypeBean.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWord", value = "对象名称包含的关键词", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/objectTypeList", method = RequestMethod.GET)
    public ReturnInfo getObjectTypeList(@RequestParam(required = false) String keyWord,
                                        @RequestHeader(name = "PCOP-USERID") String creator,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getObjectTypeList(keyWord={})", keyWord);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(),
                objService.getAllObjectType(keyWord));
        return ri;
    }

    @ApiOperation(value = "查询对象类型列表", notes = "根据输入的关键词，在对象名称中检索包含关键词的对象类型，分页查询", response = ObjectQueryOutput.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryConditions", value = "查询条件", required = true, dataType = "InstanceQueryInputBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/objectTypeList", method = RequestMethod.POST)
    public ReturnInfo queryObjectTypesByPage(@RequestBody InstanceQueryInputBean queryConditions, @RequestHeader(name = "PCOP-USERID") String creator,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("queryObjectTypesByPage(queryConditions={})", objectMapper.writeValueAsString(queryConditions));
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), objService.queryAllObjectsByPage(queryConditions));
        return ri;
    }

    @ApiOperation(value = "对象类型ID是否可用", notes = "若该ID的对象类型则返回false，否则返回true", response = Boolean.class)
    @RequestMapping(value = "/objectType/{objectTypeId}/isAvailable", method = RequestMethod.GET)
    public ReturnInfo isObjectTypeIdAvailable(@PathVariable String objectTypeId) {
        log.info("isObjectTypeIdAvailable(objectTypeId={})", objectTypeId);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), objService.isObjectTypeIdAvailable(objectTypeId));
        return ri;
    }

}
