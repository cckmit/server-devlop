package com.glodon.pcop.cimsvc.controller.normal;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.AddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.AddObjectTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdateObjectTypeOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.DouplicateObjectIdException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.RelationshipQueryInputBean;
import com.glodon.pcop.cimsvc.model.v2.BasePageableQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.BaseQueryInputBean;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelatedInstancesQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeRelationshipQueryInput;
import com.glodon.pcop.cimsvc.model.v2.RelatedInstancesBean;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import com.glodon.pcop.cimsvc.service.v2.ObjectTypesService;
import com.glodon.pcop.cimsvc.service.v2.RelationshipsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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

import java.util.List;
import java.util.Map;

/**
 * @author yuanjk
 * @description 对象模型定义相关接口
 * @date 2018/11/30 17:04
 */
@Api(tags = "对象模型定义")
@RestController
@RequestMapping("/objectTypes")
public class ObjectTypeController {
    private static Logger log = LoggerFactory.getLogger(ObjectTypeController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectTypesService objectTypesService;

    @Autowired
    private RelationshipsService relationshipService;

    @Autowired
    private InstancesService instancesService;

    @ApiOperation(value = "新增对象模型定义", notes = "新增对象模型定义", response = AddObjectTypeOutputBean.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ReturnInfo addInfoObjectDef(@RequestBody AddObjectTypeInputBean objectType,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, DouplicateObjectIdException {
        log.info("addInfoObjectDef(objectTypeVO={})", objectType);
        AddObjectTypeOutputBean outputBean = objectTypesService.addObjectType(tenantId, objectType);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, outputBean);
        return ri;
    }

    @ApiOperation(value = "查询对象模型定义", notes = "根据模型ID，查询对象模型定义", response = ObjectTypeEntity.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "dataSetType", value = "属性集类型", required = false, dataType = "String"
            , allowableValues = "OBJECT, INSTANCE", paramType = "query")})
    @RequestMapping(value = "/{objectTypeId}", method = RequestMethod.GET)
    public ReturnInfo getInfoObjectDef(@PathVariable String objectTypeId,
                                       @RequestParam(required = false) String dataSetType,
                                       @RequestParam(required = false) boolean isIncludedDataSet,
                                       @RequestParam(required = false) boolean isIncludedProperty,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getInfoObjectDef(objectTypeId={}, dataSetType={}, isIncludedDataSet={}, isIncludedProperty={})",
                objectTypeId, dataSetType, isIncludedDataSet, isIncludedProperty);
        ObjectTypeEntity entity = objectTypesService.getObjectType(tenantId, objectTypeId, isIncludedDataSet,
                isIncludedProperty, dataSetType);
        ReturnInfo ri;
        if (entity == null) {
            ri = new ReturnInfo(CodeAndMsg.E05040404);
        } else {
            ri = new ReturnInfo(CodeAndMsg.E05000200, entity);
        }
        return ri;
    }

    @ApiOperation(value = "更新对象模型定义", notes = "更新对象模型定义", response = UpdateObjectTypeOutputBean.class)
    @RequestMapping(value = "/{objectTypeId}", method = RequestMethod.PUT)
    public ReturnInfo updateInfoObjectDef(@PathVariable String objectTypeId,
                                          @RequestBody UpdateObjectTypeInputBean objectType,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException, DataServiceModelRuntimeException {
        log.info("updateInfoObjectDef(objectTypeId={}, objectTypeVO={})", objectTypeId,
                objectMapper.writeValueAsString(objectType));
        UpdateObjectTypeOutputBean outputBean = objectTypesService.updateObjectType(tenantId, objectTypeId, objectType);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, outputBean);
        return ri;
    }

    @ApiOperation(value = "删除对象模型定义", notes = "删除对象模型定义", response = Boolean.class)
    @RequestMapping(value = "/{objectTypeId}", method = RequestMethod.DELETE)
    public ReturnInfo deleteInfoObjectDef(@PathVariable String objectTypeId,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        log.info("deleteInfoObjectDef(objectTypeId={})", objectTypeId);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, objectTypesService.deleteObjectType(tenantId,
                objectTypeId));
        return ri;
    }

    @ApiOperation(value = "对象类型ID是否可用", notes = "若该ID的对象类型则返回false，否则返回true", response = Boolean.class)
    @RequestMapping(value = "/{objectTypeId}/available", method = RequestMethod.GET)
    public ReturnInfo isObjectTypeIdAvailable(@PathVariable String objectTypeId) {
        log.info("isObjectTypeIdAvailable(objectTypeId={})", objectTypeId);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, objectTypesService.isObjectTypeIdAvailable(objectTypeId));
        return ri;
    }

    @ApiOperation(value = "查询对象类型列表", notes = "根据输入的关键词，在对象显示名称中检索包含关键词的对象类型，分页查询", response =
            ObjectTypeQueryOutput.class)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnInfo queryObjectTypesByPage(@RequestBody ObjectTypeQueryInput conditions,
                                             @RequestHeader(name = "PCOP-USERID") String creator,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, JsonProcessingException {
        log.info("queryObjectTypesByPage(conditions={})", objectMapper.writeValueAsString(conditions));
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, objectTypesService.queryObjectTypes(tenantId, conditions));
        return ri;
    }

    @ApiOperation(value = "查询对象关系定义", notes = "获取与指定对象类型相关的所有对象关系定义", responseContainer = "List", response =
            RelationshipEntity.class)
    @RequestMapping(value = "/{objectTypeId}/relationships", method = RequestMethod.POST)
    public ReturnInfo getRelationshipsByObjectTypeName(@PathVariable(name = "objectTypeId") String objectTypeId,
                                                       @RequestBody ObjectTypeRelationshipQueryInput queryConditions,
                                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws CimDataEngineRuntimeException, JsonProcessingException {
        log.info("getRelationshipsByObjectTypeName(objectTypeId={}, queryConditions={})", objectTypeId,
                objectMapper.writeValueAsString(queryConditions));
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, objectTypesService.queryRelationships(tenantId,
                objectTypeId, queryConditions));
        return ri;
    }

    @ApiOperation(value = "查询指定对象类型所有实例的关联实例", notes = "指定对象类型，关系类型，关系方向（可选），目标对象类型（可选）", response =
            BasePageableQueryOutput.class)
    @RequestMapping(value = "/{objectTypeId}/relationTypes/{relationTypeName}/instances", method = RequestMethod.POST)
    public ReturnInfo getObjectTypeRelatedInstances(@PathVariable(name = "objectTypeId") String objectTypeId,
                                                    @PathVariable(name = "relationTypeName") String relationTypeName,
                                                    @RequestBody ObjectTypeRelatedInstancesQueryInput queryConditions,
                                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException, DataServiceModelRuntimeException {
        log.info("getObjectTypeRelatedInstances(objectTypeId={}, relationTypeName={}, queryConditions={})",
                objectTypeId, relationTypeName, objectMapper.writeValueAsString(queryConditions));
        BasePageableQueryOutput<ObjectTypeRelatedInstancesQueryOutput> resultMapList =
                relationshipService.getRelatedInstanceByObjectType(tenantId, objectTypeId, relationTypeName,
                        queryConditions);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, resultMapList);
        return ri;
    }

    // @ApiOperation(value = "查询指定对象类型所有实例的关联实例", notes = "指定对象类型，关系类型，关系方向（可选），目标对象类型（可选）", responseContainer = "List",
    // response = ObjectTypeRelatedInstancesQueryOutput.class)
    // @RequestMapping(value = "/{objectTypeId}/relationships/{relationshipRid}/instances", method = RequestMethod.POST)
    public ReturnInfo getRelatedInstances(@PathVariable(name = "objectTypeId") String objectTypeId,
                                          @PathVariable(name = "relationshipRid") String relationshipRid,
                                          @RequestParam(required = false) String instanceRid,
                                          @RequestBody BaseQueryInputBean queryConditions,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException {
        log.info("getObjectTypeRelatedInstances(relationshipRid={}, instanceRid={}, queryConditions={})",
                relationshipRid, instanceRid, objectMapper.writeValueAsString(queryConditions));
        List<ObjectTypeRelatedInstancesQueryOutput> resultMapList = relationshipService.getRelatedInstance(tenantId,
                objectTypeId, relationshipRid, queryConditions);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, resultMapList);
        return ri;
    }

    @ApiOperation(value = "查询指定实例的关联实例的基本信息", notes = "指定对象类型，关系类型，实例RID，关系方向（可选），目标对象类型（可选）", responseContainer =
            "List", response = RelatedInstancesBean.class)
    @RequestMapping(value = "/{objectTypeId}/relationTypes/{relationTypeName}/instances/{instanceRid}/baseInfo",
            method = RequestMethod.POST)
    public ReturnInfo getRelatedObjectsBaseInfoByRidAndType(@PathVariable(name = "objectTypeId") String objectTypeId,
                                                            @PathVariable(name = "relationTypeName") String relationTypeName,
                                                            @PathVariable(name = "instanceRid") String instanceRid,
                                                            @RequestBody RelationshipQueryInputBean queryInputBean,
                                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("getRelatedObjectsBaseInfoByRidAndType(objectTypeId={}, instanceRid={}, relationTypeName={})",
                objectTypeId, instanceRid, relationTypeName);
        List<RelatedInstancesBean> resultMapList = relationshipService.getRelatedInstanceBaseInfoByRid(tenantId,
                objectTypeId, instanceRid, relationTypeName, queryInputBean);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                resultMapList);
        return ri;
    }

    @ApiOperation(value = "查询指定实例的关联实例的基本信息", notes = "指定对象类型，关系类型，实例RID，关系方向（可选），目标对象类型（可选）", responseContainer =
            "List", response = RelatedInstancesBean.class)
    @RequestMapping(value = "/{objectTypeId}/relationTypes/{relationTypeName}/instances/{instanceRid}", method =
            RequestMethod.POST)
    public ReturnInfo getRelatedObjectsGeneralInfoByRidAndType(@PathVariable(name = "objectTypeId") String objectTypeId,
                                                               @PathVariable(name = "relationTypeName") String relationTypeName,
                                                               @PathVariable(name = "instanceRid") String instanceRid,
                                                               @RequestBody RelationshipQueryInputBean queryInputBean,
                                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("getRelatedObjectsBaseInfoByRidAndType(objectTypeId={}, instanceRid={}, relationTypeName={})",
                objectTypeId, instanceRid, relationTypeName);
        List<RelatedInstancesBean> resultMapList = relationshipService.getRelatedInstanceGeneralInfoByRid(tenantId,
                objectTypeId, instanceRid, relationTypeName, queryInputBean);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                resultMapList);
        return ri;
    }

    @ApiOperation(value = "对象类型实例数量", notes = "统计指定对象类型的实例数量", response = Map.class)
    @RequestMapping(value = "/instances/count", method = RequestMethod.POST)
    public ReturnInfo countByObjectType(@RequestBody List<String> objectTypeIds,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException {
        log.info("countByObjectType(objectTypeIds={})", objectMapper.writeValueAsString(objectTypeIds));
        Map<String, Long> result = instancesService.countByObjectType(tenantId, objectTypeIds);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "检查对象模型是否存在，若不存在则创建", notes = "检查对象模型是否存在，若不存在则创建", response =
            CheckAndAddObjectTypeOutputBean.class, responseContainer = "list")
    @RequestMapping(value = "/checkAndCreate", method = RequestMethod.POST)
    public ReturnInfo checkAndCreateObjectDef(@RequestBody List<CheckAndAddObjectTypeInputBean> objectTypes,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("checkAndCreateObjectDef(objectTypes={})", StringUtils.join(objectTypes, ','));
        List<CheckAndAddObjectTypeOutputBean> outputBean = objectTypesService.checkAndCreateObjectDef(tenantId,
                objectTypes);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, outputBean);
        return ri;
    }

}
