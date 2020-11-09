package com.glodon.pcop.cimsvc.controller.relationship;

import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationInputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationOutputBean;
import com.glodon.pcop.cim.common.model.relationship.RelatedInstancesOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.service.relationship.InstanceRelationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "实例关系")
@RestController
@RequestMapping(path = "/instances/relations")
public class RelationController {
    private static Logger log = LoggerFactory.getLogger(RelationController.class);

    @Autowired
    private InstanceRelationService relationService;

    @ApiOperation(value = "创建实例之间的关系", notes = "创建实例之间的关系", response = AddInstanceRelationOutputBean.class)
    @RequestMapping(method = RequestMethod.POST)
    public ReturnInfo createInstanceRelations(@Validated @RequestBody AddInstanceRelationInputBean instanceRelations,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("createInstanceRelations(instanceRelations={})", instanceRelations);
        AddInstanceRelationOutputBean data = relationService.createRelations(tenantId, instanceRelations);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "删除实例之间的关系", notes = "删除实例之间的关系", response = Map.class)
    @RequestMapping(method = RequestMethod.DELETE)
    public ReturnInfo deleteInstanceRelations(@RequestBody List<String> relationRids,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteInstanceRelations(relationRids={})", relationRids);
        Map<String, Boolean> data = relationService.deleteRelationsByRid(tenantId, relationRids);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询直接关联的实例", notes = "查询直接关联的实例", response = List.class)
    @RequestMapping(method = RequestMethod.GET)
    public ReturnInfo queryDirectRelatedInstances(@RequestParam String objectTypeId,
                                                  @RequestParam String instanceId,
                                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("queryDirectRelatedInstances(objectTypeId={}, instanceId={})", objectTypeId, instanceId);
        List<RelatedInstancesOutputBean> data = relationService.queryDirectRelatedInstances(tenantId, objectTypeId,
                instanceId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

}
