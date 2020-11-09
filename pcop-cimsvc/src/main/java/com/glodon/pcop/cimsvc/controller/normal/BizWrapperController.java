package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.v2.CollectionQueryOutput;
import com.glodon.pcop.cimsvc.service.BizWrapperService;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import com.glodon.pcop.cimsvc.service.v2.RelationshipsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Api(tags = "对象模型业务封装")
@RestController
@RequestMapping(value = "/bizWrapper")
public class BizWrapperController {
    private static Logger log = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InstancesService instancesService;
    @Autowired
    private RelationshipsService relationshipService;

    @Autowired
    BizWrapperService bizWrapperService;

    @ApiOperation(value = "由数据类型查询相应数据", notes = "由数据类型查询相应数据", response = CollectionQueryOutput.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "dataSetType", value = "数据集类型", defaultValue = "INSTANCE",
            allowableValues = "INSTANCE, OBJECT", dataType = "String", paramType = "query")})
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnInfo queryGisBimData(@RequestParam String objectTypeId,
                                              @RequestParam(required = false) String dataSetName,
                                              @RequestParam(defaultValue = "INSTANCE") String dataSetType,
                                              @RequestParam(required = false) String instanceRid,
                                              @RequestParam String elementType,
                                              @RequestParam String elementId,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("queryInstanceCollection(objectTypeId={}, dataSetName={}, dataSetType={}, instanceRid={}, " +
                "queryConditions={})", objectTypeId, dataSetName, dataSetType, instanceRid);

        Object result = bizWrapperService.queryGisBim(objectTypeId, elementId, dataSetName, dataSetType,
                                            instanceRid,  tenantId, elementType);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

}
