package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.OutputQueryFroNameBean;
import com.glodon.pcop.cimsvc.model.OutputQueryFroNameVO;
import com.glodon.pcop.cimsvc.model.v2.*;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Api(tags = "对象实例批处理操作")
@RestController
@RequestMapping("/objectTypes/{objectTypeId}/instances")
public class InstancesController {
    private static Logger log = LoggerFactory.getLogger(InstancesController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InstancesService instancesService;

    @CrossOrigin({"*"})
    @ApiOperation(value = "查询实例数据，single", notes = "查询实例数据，single", response = SingleQueryOutput.class)
    @RequestMapping(value = "/query", method = {RequestMethod.POST, RequestMethod.GET})
    public ReturnInfo queryInstanceSingle(@PathVariable String objectTypeId,
                                          @RequestParam(required = false) String dataSetName,
                                          @Valid @RequestBody InstancesQueryInput queryConditions,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId)
            throws InputErrorException {
        log.info("queryInstanceSingle(objectTypeId={}, dataSetName={}, queryConditions={}, tenantId={})", objectTypeId,
                dataSetName, queryConditions, tenantId);
        SingleQueryOutput result = instancesService.queryInstanceSingle(tenantId, objectTypeId.trim(), dataSetName,
                queryConditions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "查询实例数据，批量返回实例名称", notes = "查询实例数据，批量返回实例名称", response = Object.class)
    @PostMapping(value = "/queryForName")
    public ReturnInfo queryForName(@PathVariable String objectTypeId,
                                   @Valid @RequestBody List<String> cimIdList,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        OutputQueryFroNameVO result = instancesService.queryForMame(tenantId, objectTypeId.trim(), cimIdList);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;

    }

    @ApiOperation(value = "根据RID查询实例数据", notes = "根据RID查询实例数据", response = Object.class)
    @PostMapping(value = "/queryByRID/{rid}")
    public ReturnInfo queryByRID(@PathVariable String objectTypeId,
                                 @PathVariable String rid,
                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        SingleInstancesQueryOutput result = instancesService.queryByRID(tenantId, objectTypeId, rid);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;

    }


    @ApiOperation(value = "新增实例数据，single", notes = "新增实例数据，single，批量", response = BatchDataOperationResult.class)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ReturnInfo addInstanceSingle(@PathVariable String objectTypeId,
                                        @RequestParam(required = false, defaultValue = "false") boolean isAddRelation,
                                        @RequestBody List<Map<String, Object>> objectValueList,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("addInstanceSingle(userId={}, objectTypeId={}, objectValueList={})", userId, objectTypeId, objectMapper.writeValueAsString(objectValueList));
        List<InfoObjectValue> infoObjectValueList = instancesService.addSingleObjectValuesFormal(objectValueList);
        if (StringUtils.isNotBlank(userId) && CollectionUtils.isNotEmpty(infoObjectValueList)) {
            for (InfoObjectValue objectValue : infoObjectValueList) {
                Map<String, Object> baseValues = objectValue.getBaseDatasetPropertiesValue();
                baseValues.put(CimConstants.BaseDataSetKeys.CREATOR, userId);
            }
        }

        BatchDataOperationResult result = instancesService.addInstanceSingle(tenantId, objectTypeId, infoObjectValueList,  isAddRelation);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }

    @CrossOrigin({"*"})
    @ApiOperation(value = "更新实例数据，single", notes = "更新实例数据，single，批量", response = BatchDataOperationResult.class)
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT})
    public ReturnInfo updateInstanceSingle(@PathVariable String objectTypeId,
                                           @RequestBody List<Map<String, Object>> objectValueList,
                                           @RequestHeader(name = "PCOP-USERID") String userId,
                                           @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
        log.info("updateInstanceSingle(objectTypeId={}, objectValueList={}, tenantId={})", objectTypeId,
                StringUtils.join(objectValueList, ','), tenantId);
        BatchDataOperationResult result = instancesService.updateInstanceSingle(tenantId, objectTypeId,
                objectValueList);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "删除实例数据，single", notes = "删除实例数据，single，批量", response = BatchDataOperationResult.class)
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ReturnInfo deleteInstanceSingle(@PathVariable String objectTypeId,
                                           @RequestBody List<String> instanceRids,
                                           @RequestHeader(name = "PCOP-USERID") String userId,
                                           @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteInstanceSingle(objectTypeId={}, instanceRids={})", objectTypeId, instanceRids);
        BatchDataOperationResult result = instancesService.deleteInstanceSingle(tenantId, objectTypeId, instanceRids);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "查询实例数据，collection", notes = "查询实例数据，collection", response = CollectionQueryOutput.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "dataSetType", value = "数据集类型", defaultValue = "INSTANCE",
            allowableValues = "INSTANCE, OBJECT", dataType = "String", paramType = "query")})
    @RequestMapping(value = "/{dataSetName}/query", method = RequestMethod.POST)
    public ReturnInfo queryInstanceCollection(@PathVariable String objectTypeId,
                                              @PathVariable String dataSetName,
                                              @RequestParam(defaultValue = "INSTANCE") String dataSetType,
                                              @RequestParam(required = false) String instanceRid,
                                              @RequestBody InstancesQueryInput queryConditions,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("queryInstanceCollection(objectTypeId={}, dataSetName={}, dataSetType={}, instanceRid={}, " +
                "queryConditions={})", objectTypeId, dataSetName, dataSetType, instanceRid, queryConditions);
        CollectionQueryOutput result = new CollectionQueryOutput();
        if (dataSetType.trim().toUpperCase().equals("INSTANCE")) {
            result = instancesService.queryInstanceCollection(tenantId, objectTypeId, instanceRid,
                    dataSetName, queryConditions);
        } else if (dataSetType.trim().toUpperCase().equals("OBJECT")) {
            result = instancesService.queryObjectCollection(tenantId, objectTypeId, instanceRid,
                    dataSetName, queryConditions);
        } else {
            log.error("not support data set type: [{}]", dataSetType);
        }
        // CollectionQueryOutput result = instancesService.queryInstanceCollection(tenantId, objectTypeId, instanceRid,
        //         dataSetName, queryConditions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "新增实例数据，collection", notes = "新增实例数据，collection，批量", response =
            BatchDataOperationResult.class)
    @RequestMapping(value = "/{dataSetName}/add", method = RequestMethod.POST)
    public ReturnInfo addInstanceCollection(@PathVariable String objectTypeId,
                                            @RequestParam String instanceRid,
                                            @PathVariable String dataSetName,
                                            @RequestBody List<Map<String, Object>> collectionDatasetValues,
                                            @RequestHeader(name = "PCOP-USERID") String userId,
                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException, DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("addInstanceSingle(objectTypeId={}, instanceRid={}, dataSetName={}, collectionDatasetValues={})",
                objectTypeId, instanceRid, dataSetName, objectMapper.writeValueAsString(collectionDatasetValues));
        BatchDataOperationResult result = instancesService.addInstanceCollection(tenantId, objectTypeId, instanceRid,
                dataSetName, collectionDatasetValues);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "删除实例数据，collection", notes = "删除实例数据，collection，批量", response =
            BatchDataOperationResult.class)
    @RequestMapping(value = "/{dataSetName}/delete", method = RequestMethod.POST)
    public ReturnInfo deleteInstanceCollection(@PathVariable String objectTypeId,
                                               @RequestParam String instanceRid,
                                               @PathVariable String dataSetName,
                                               @RequestBody List<String> dataSetInstanceRids,
                                               @RequestHeader(name = "PCOP-USERID") String userId,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws JsonProcessingException, DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("addInstanceSingle(objectTypeId={}, instanceRid={}, dataSetName={}, dataSetInstanceRids={})",
                objectTypeId, instanceRid, dataSetName, objectMapper.writeValueAsString(dataSetInstanceRids));
        BatchDataOperationResult result = instancesService.deleteInstanceCollection(tenantId, objectTypeId,
                instanceRid, dataSetName, dataSetInstanceRids);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "多源异构融合查询")
    @PostMapping(value = "/compositeQuery")
    public ReturnInfo compositeQuery(
            @PathVariable String objectTypeId,
            @RequestBody InstancesQueryInput queryConditions,
            @RequestParam List<String> dataSetNames,
            @RequestHeader(name = "PCOP-USERID") String userId,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException,
            EntityNotFoundException {
        log.info("compositeQuery(objectTypeId={},queryConditions={},dataSetNames={})", objectTypeId, queryConditions,
                dataSetNames);
        CompositeQueryOutput result = instancesService.compositeQuery(objectTypeId, queryConditions, dataSetNames,
                userId, tenantId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

    @ApiOperation(value = "根据条件删除实例数据及其关联实例数据，single", notes = "根据条件删除实例数据及其关联实例数据，single，批量", response = BatchDataOperationResult.class)
    @RequestMapping(value = "/deleteByCondition", method = RequestMethod.POST)
    public ReturnInfo deleteInstanceSingleByCondition(@PathVariable String objectTypeId,
                                                      @RequestBody DeleteByConditionInputBean deleteCondition,
                                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws InputErrorException {
        log.info("deleteInstanceSingleByCondition(objectTypeId={}, deleteCondition={})", objectTypeId, deleteCondition);
        BatchDataOperationResult result = instancesService.deleteInstanceSingleByCondition(tenantId, objectTypeId, deleteCondition);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }

}
