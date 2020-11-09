package com.glodon.pcop.cimsvc.controller.normal;

import com.glodon.pcop.cim.common.model.entity.ActionEntity;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.ActionExecutionResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.service.v2.ActionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "对象模型定义")
@RestController
@RequestMapping(value = "/objectTypes/{objectTypeId}")
public class ActionController {
    private static Logger log = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    private ActionService actionService;

    @ApiOperation(value = " 采取行动", notes = "对已定义的Action，采取行动", response = ActionExecutionResult.class,
            responseContainer = "List")
    @RequestMapping(value = "/action", method = RequestMethod.POST)
    public ReturnInfo takeAction(@PathVariable String objectTypeId,
                                 @RequestParam(required = false) String instanceRid,
                                 @RequestParam String actionName,
                                 @RequestBody(required = false) Map<String, Object> actionPayload,
                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, InputErrorException {
        log.info("takeAction(objectTypeId={},actionName={}, instanceRid={}, actionPayload={})", objectTypeId,
                actionName, instanceRid, actionPayload);
        if (StringUtils.isBlank(actionName)) {
            throw new InputErrorException("input error, action name is mandatory");
        }

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                actionService.takeAction(tenantId, objectTypeId, instanceRid, actionName, actionPayload));
        return ri;
    }

    @ApiOperation(value = " Action列表", notes = "查询指定对象类型已经定义的action", response = ActionEntity.class,
            responseContainer = "List")
    @RequestMapping(value = "/actions", method = RequestMethod.GET)
    public ReturnInfo actionList(@PathVariable String objectTypeId,
                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("actionList(objectTypeId={})", objectTypeId);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                actionService.getActions(tenantId, objectTypeId));
        return ri;
    }

}
