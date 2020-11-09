package com.glodon.pcop.cimsvc.controller.deprecate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.FileImportTaskInputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cimapi.DataImportApi;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimapi.model.InstanceQueryOutput;
import com.glodon.pcop.cimsvc.exception.PropertyMappingException;
import com.glodon.pcop.cimsvc.exception.ShpFileParserException;
import com.glodon.pcop.cimsvc.model.FileStructureBean;
import com.glodon.pcop.cimsvc.model.InputInstancesBean;
import com.glodon.pcop.cimsvc.model.InstanceBean;
import com.glodon.pcop.cimsvc.model.PropertyMappingBeanGis;
import com.glodon.pcop.cimsvc.service.FileImportService;
import com.glodon.pcop.cimsvc.service.OldInstanceService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
// @Api(tags = "v1--文件数据导入")
// @RestController
@RequestMapping("/abort")
public class DataImportController implements DataImportApi {
    static Logger log = LoggerFactory.getLogger(DataImportController.class);

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private OldInstanceService instanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "文件上传并解析", notes = "上传本地文件到Minio服务器，然后解析并返回文件结构", response = FileStructureBean.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "file", value = "shp文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/fileUploadAndParser", method = RequestMethod.POST)
    public ReturnInfo fileUploadAndParser(@RequestParam(name = "fileType") String fileType,
                                          @RequestParam(name = "file") MultipartFile file) throws ShpFileParserException {
        log.info("fileUploadAndParser(fileType={})", fileType);
        FileStructureBean fsb = new FileStructureBean();
        try {
            fsb = fileImportService.fileUploadAndParser(fileType, file);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                fsb);
        return ri;
    }

    @ApiOperation(value = "创建上传任务", notes = "根据输入的属性匹配结果，若没有属性匹配则为对象创建新的属性集，导入数据到CIM库", response = Boolean.class)
    // @RequestMapping(value = "/task", method = RequestMethod.POST)
    public ReturnInfo createImportTask(@RequestParam(name = "fileType", defaultValue = "SHP") String fileType,
                                       @RequestBody PropertyMappingBeanGis propertyMapping,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws PropertyMappingException {
        log.info("createImportTask(fileType={})", fileType);
        String rs = instanceService.createTask(fileType, propertyMapping);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), rs);
        return ri;
    }

    @ApiOperation(value = "实例数据录入", notes = "通过界面手动录入一个对象的实例数据", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceData", value = "对象的属性值", required = true, dataType =
                    "InputInstancesBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/importManually", method = RequestMethod.POST)
    public ReturnInfo importManually(@RequestBody InputInstancesBean instanceData,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("importManually()");
        boolean flag = instanceService.importManually(instanceData);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                flag);
        return ri;
    }

    /**
     * 实例数据查询
     *
     * @param conditions
     * @param userId
     * @param tenantId
     * @return
     */
    @Override
    public ReturnInfo instanceDataQuery(@RequestBody InstanceQueryInputBean conditions,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("queryInstanceSingle(conditions={})", conditions);
        InstanceQueryOutput instances = instanceService.queryInstanceData(conditions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                instances);
        return ri;
    }

    @ApiOperation(value = "更新实例", notes = "根据实例ID，更新实例数据", response = Boolean.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "instanceId", value = "实例ID", required = true, dataType = "string",
            paramType = "path"),
            @ApiImplicitParam(name = "instance", value = "实例数据", required = true, dataType = "InstanceBean",
                    paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.PUT)
    public ReturnInfo updateInstanceData(@PathVariable String instanceId, @RequestBody InstanceBean instance,
                                         @RequestHeader(name = "PCOP-USERID") String userId,
                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("updateInstanceData(instanceId={})", instanceId);
        boolean flag = instanceService.updateInstanceData(instanceId, instance);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                flag);
        return ri;
    }

    @ApiOperation(value = "删除实例", notes = "根据实例ID，删除实例数据", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectTypeId", value = "对象类型ID", required = true, dataType = "string",
                    paramType = "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.DELETE)
    public ReturnInfo deleteInstanceData(@PathVariable String instanceId,
                                         @RequestParam(name = "objectTypeId") String objectTypeId,
                                         @RequestHeader(name = "PCOP-USERID") String userId,
                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteInstanceData(instanceId={}, objectTypeId={})", instanceId, objectTypeId);
        boolean flag = instanceService.deleteInstanceData(instanceId, objectTypeId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                flag);
        return ri;
    }

    @ApiOperation(value = "图像上传", notes = "上传图像文件到CIM", response = Boolean.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "objectTypeId", value = "对象类型id", required = true, dataType =
            "string", paramType = "body"),
            @ApiImplicitParam(name = "dateSetId", value = "属性集id", required = true, dataType = "string", paramType =
                    "body"),
            @ApiImplicitParam(name = "propertyId", value = "属性名称", required = true, dataType = "string", paramType =
                    "body"),
            @ApiImplicitParam(name = "instanceId", value = "实例ID", required = true, dataType = "string", paramType =
                    "body"),
            @ApiImplicitParam(name = "file", value = "图像文件", required = true, dataType = "MultipartFile", paramType =
                    "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public ReturnInfo imageUpload2Cim(@RequestPart String objectTypeId,
                                      @RequestPart String dateSetId,
                                      @RequestPart String propertyId,
                                      @RequestPart String instanceId,
                                      @RequestPart MultipartFile file,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException,
            CimDataEngineRuntimeException {
        log.info("imageUpload2Cim(objectTypeId={}, dateSetId={}, propertyId={}, instanceId={})", objectTypeId,
                dateSetId, propertyId, instanceId);
        boolean flag = false;
        try {
            flag = fileImportService.imageUpload2Cim(instanceId, propertyId, file);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                flag);
        return ri;
    }

    @ApiOperation(value = "创建文件导入任务", notes = "根据输入的属性匹配结果，若没有属性匹配则为对象创建新的属性集，导入数据到CIM库", response = Boolean.class)
    @RequestMapping(value = "/task/import", method = RequestMethod.POST)
    public ReturnInfo createNewImportTask(@RequestBody FileImportTaskInputBean importTaskInput,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("createNewImportTask(importTaskInput={})", objectMapper.writeValueAsString(importTaskInput));
        importTaskInput.setTenantId(tenantId);
        String rs = instanceService.createTask(importTaskInput);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), rs);
        return ri;
    }

}
