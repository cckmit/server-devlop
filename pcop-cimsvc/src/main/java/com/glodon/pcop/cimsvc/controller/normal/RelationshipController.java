package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.RelationTypeBean;
import com.glodon.pcop.cimsvc.service.v2.RelationshipsService;
import io.swagger.annotations.Api;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuanjk
 * @description 对象类型关系定义相关接口
 * @date 2018/11/30 17:06
 */
@Api(tags = "对象模型关系定义")
@RestController
@RequestMapping("/relationships")
public class RelationshipController {
    private static Logger log = LoggerFactory.getLogger(RelationshipController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RelationshipsService relationshipService;

    @ApiOperation(value = "获取所有关系类型", notes = "获取所有关系类型", response = List.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/relationTypes", method = RequestMethod.GET)
    public ReturnInfo getAllRelationTypes(@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getAllRelationTypes()");
        List<RelationTypeBean> allRelationTypeBeans = relationshipService.getAllRelationTypes(tenantId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                allRelationTypeBeans);
        return ri;
    }

    @ApiOperation(value = "新增对象关系定义", notes = "新增对象关系定义", response = RelationshipEntity.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ReturnInfo addRelationShip(@RequestBody RelationshipEntity relationship,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("addRelationship(relationship={})", objectMapper.writeValueAsString(relationship));

        RelationshipEntity results = relationshipService.addRelationshipDef(tenantId, relationship);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "更新对象关系定义", notes = "更新对象关系定义", response = RelationshipEntity.class)
    @RequestMapping(value = "/{relationshipRid}", method = RequestMethod.PUT)
    public ReturnInfo updateRelationShip(@PathVariable(name = "relationshipRid") String relationshipRid,
                                         @RequestBody RelationshipEntity relationship,
                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("updateRelationship(relationshipRid={}, relationship={})", relationshipRid, relationship);
        RelationshipEntity results = relationshipService.updateRelationshipDef(tenantId, relationshipRid, relationship);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "删除对象关系定义", notes = "删除对象关系定义", response = Boolean.class)
    @RequestMapping(value = "/{relationshipRid}", method = RequestMethod.DELETE)
    public ReturnInfo deleteRelationShip(@PathVariable(name = "relationshipRid") String relationshipRid,
                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteRelationship(relationshipRid={})", relationshipRid);
        boolean flag = relationshipService.deleteRelationshipDef(tenantId, relationshipRid);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, flag);
        return ri;
    }

    @ApiOperation(value = "查询对象关系定义", notes = "查询对象关系定义", response = RelationshipMappingVO.class)
    @RequestMapping(value = "/{relationshipRid}", method = RequestMethod.GET)
    public ReturnInfo getRelationShip(@PathVariable(name = "relationshipRid") String relationshipRid,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws CimDataEngineRuntimeException {
        log.info("getRelationShip(relationshipRid={})", relationshipRid);
        RelationshipMappingVO data = relationshipService.getRelationshipDef(tenantId, relationshipRid);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

}
