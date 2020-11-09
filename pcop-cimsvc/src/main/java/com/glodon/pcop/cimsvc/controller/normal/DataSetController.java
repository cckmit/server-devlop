package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateDataSetInputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.service.v2.DataSetsService;
import io.swagger.annotations.Api;
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


/**
 * @author yuanjk
 * @description 属性集和属性定义相关接口
 * @date 2018/11/30 17:03
 */
@Api(tags = "属性集和属性定义")
@RestController
@RequestMapping("/objectTypes/{objectTypeId}/dataSet")
public class DataSetController {
    private static Logger log = LoggerFactory.getLogger(DataSetController.class);

    @Autowired
    private DataSetsService dataSetService;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "新增属性集定义", notes = "新增属性集定义，包括属性集和该属性集拥有的属性", response = DataSetEntity.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ReturnInfo addDataSetAndPropertyDef(@PathVariable String objectTypeId,
                                               @RequestBody AddDataSetInputBean dataSet,
                                               @RequestHeader(name = "PCOP-USERID") String userId,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        log.info("addDataSetAndPropertyDef(objectTypeId={}, dataSet={})", objectTypeId, dataSet);
        DataSetEntity results = dataSetService.addDataSetAndPropertyDef(tenantId, objectTypeId, dataSet);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "更新属性集定义", notes = "更新属性集定义，包括属性集和该属性集拥有的属性", response = DataSetEntity.class)
    @RequestMapping(value = "/{dataSetRid}", method = RequestMethod.PUT)
    public ReturnInfo updateDataSetAndPropertyDef(@PathVariable String objectTypeId,
                                                  @RequestBody UpdateDataSetInputBean dataSet,
                                                  @PathVariable String dataSetRid,
                                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        log.info("updateDataSetAndPropertyDef(objectTypeId={}, dataSetRid={}, dataSet={})", objectTypeId, dataSetRid,
                dataSet);
        DataSetEntity results = dataSetService.updateDataSetAndPropertyDef(tenantId, objectTypeId, dataSetRid, dataSet);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

    @ApiOperation(value = "删除属性集定义", notes = "删除属性集定义，包括属性集和该属性集拥有的属性", response = boolean.class)
    @RequestMapping(value = "/{dataSetRid}", method = RequestMethod.DELETE)
    public ReturnInfo deleteDataSetAndPropertyDef(@PathVariable String objectTypeId,
                                                  @PathVariable String dataSetRid,
                                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException {
        log.info("deleteDataSetAndPropertyDef(objectTypeId={}, dataSetRid={})", objectTypeId, dataSetRid);
        boolean results = dataSetService.deleteDataSetAndPropertyDef(tenantId, objectTypeId, dataSetRid);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                results);
        return ri;
    }

    @ApiOperation(value = "查询属性集定义", notes = "查询属性集定义，包括属性集和该属性集拥有的属性", response = DataSetEntity.class)
    @RequestMapping(value = "/{dataSetRid}", method = RequestMethod.GET)
    public ReturnInfo getDataSetAndPropertyDef(@PathVariable String objectTypeId,
                                               @PathVariable String dataSetRid,
                                               @RequestHeader(name = "PCOP-USERID") String userId,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getDataSetAndPropertyDef(objectTypeId={}, dataSetRid={})", objectTypeId, dataSetRid);
        DataSetEntity results = dataSetService.getDataSetAndPropertyDef(tenantId, objectTypeId, dataSetRid);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                results);
        return ri;
    }


    @ApiOperation(value = "新增Metadata属性集定义", notes = "新增属性集定义，包括属性集和该属性集拥有的属性", response = DataSetEntity.class)
    @RequestMapping(value = "/metadata", method = RequestMethod.POST)
    public ReturnInfo addMetadataDataSetAndPropertyDef(@PathVariable String objectTypeId,
                                                       @RequestBody AddDataSetInputBean dataSet,
                                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        log.info("addDataSetAndPropertyDef(objectTypeId={}, dataSet={})", objectTypeId,
                objectMapper.writeValueAsString(dataSet));
        DataSetEntity results = dataSetService.addDataSetAndPropertyDef(tenantId, objectTypeId, dataSet);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, results);
        return ri;
    }

}
