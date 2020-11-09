package com.glodon.pcop.cimsvc.controller.deprecate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.DouplicateNameException;
import com.glodon.pcop.cimsvc.exception.EntityAddException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputNotEnoughException;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeOutputBean;
import com.glodon.pcop.cimsvc.model.v2.LinkObjectAndInstanceInputBean;
import com.glodon.pcop.cimsvc.model.v2.LinkObjectAndInstanceOutputBean;
import com.glodon.pcop.cimsvc.service.v2.IndustryTypesService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author yuanjk
 * @description 行业分类定义相关接口
 */
// @Api(tags = "v1--数据管理目录树，行业分类")
// @RestController
@RequestMapping("/abort/industryTypes")
public class IndustryTypeMetadataController {
    static Logger log = LoggerFactory.getLogger(IndustryTypeMetadataController.class);

    @Autowired
    private IndustryTypesService industryTypeService;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "行业分类的属性集定义", notes = "行业分类的的属性集定义", response = DataSetEntity.class, responseContainer = "List")
    @RequestMapping(value = "/dataSet/def", method = RequestMethod.GET)
    public ReturnInfo getIndustryTypeDataSetDef(@RequestParam(required = false) String dataSetName,
                                                @RequestParam(defaultValue = "true") Boolean isIncludedProperty,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getIndustryTypeDataSetDef(dataSetName={}, isIncludedProperty={})", dataSetName, isIncludedProperty);
        List<DataSetEntity> results = industryTypeService.getDataSetDefs(tenantId, dataSetName, isIncludedProperty);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), results);
        return ri;
    }

    @ApiOperation(value = "新增行业分类和元数据", notes = "根据输入的对象新增行业分类模型及其元数据", response = Boolean.class)
    @RequestMapping(value = "/dataSet", method = RequestMethod.POST)
    public ReturnInfo addIndustryTypeDataSet(@RequestBody Map<String, Object> values,
                                             @RequestParam(defaultValue = "true") Boolean isGeneral,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, DouplicateNameException {
        log.info("addIndustryTypeDataSet(values={})", objectMapper.writeValueAsString(values));
        String results = industryTypeService.addIndustryTypeDataSet(tenantId, userId, values,isGeneral);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, code.getMsg(), results);
        return ri;
    }

    @ApiOperation(value = "行业分类详情分属性集", notes = "根据输入行业分类标识和属性集name，分属性集获取行业分类详情分属性集", response = Map.class)
    @RequestMapping(value = "/{industryTypeRid}/dataSet", method = RequestMethod.GET)
    public ReturnInfo getIndustryTypeDataSet(@PathVariable String industryTypeRid,
                                             @RequestParam(defaultValue = "IndustryMetadataBaseDataSet") String dataSetName,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getIndustryTypeDataSet(industryTypeRid={}，dataSetName={}, tenantId={}, userId={})", industryTypeRid, dataSetName, tenantId, userId);
        Map<String, Object> results = industryTypeService.getIndustryTypeDataSet(tenantId, industryTypeRid, dataSetName);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05040404;
        }
        ReturnInfo ri = new ReturnInfo(code, code.getMsg(), results);
        return ri;
    }

    @ApiOperation(value = "更新行业分类元数据", notes = "根据输入的typeId和对象更新行业分类元数据", response = Boolean.class)
    @RequestMapping(value = "/{industryTypeRid}/dataSet", method = RequestMethod.PUT)
    public ReturnInfo updateIndustryTypeDataSet(@PathVariable String industryTypeRid,
                                                @RequestBody Map<String, Object> values,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, DouplicateNameException {
        log.info("updateIndustryType(industryTypeRid={}, industryType={})", industryTypeRid, objectMapper.writeValueAsString(values));
        String results = industryTypeService.updateIndustryTypeDataSet(tenantId, userId, industryTypeRid, values);
        CodeAndMsg code = CodeAndMsg.E05000200;
        if (results == null) {
            code = CodeAndMsg.E05010505;
        }
        ReturnInfo ri = new ReturnInfo(code, code.getMsg(), results);
        return ri;
    }

    @ApiOperation(value = "创建目录树文件节点", notes = "创建目录树文件节点", response = TreeNodeOutputBean.class)
    @RequestMapping(value = "/{industryTypeRid}/fileNode", method = RequestMethod.POST)
    @Deprecated
    public ReturnInfo addFileNode(@PathVariable String industryTypeRid,
                                  @RequestBody Map<String, Object> fileMetadata,
                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, EntityNotFoundException, EntityAddException, InputNotEnoughException {
        log.info("addFileNode(industryTypeRid={}, fileMetadata={})", industryTypeRid, objectMapper.writeValueAsString(fileMetadata));
        TreeNodeOutputBean nodeOutputBean = industryTypeService.addFileNode(tenantId, userId, industryTypeRid, fileMetadata);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), nodeOutputBean);
        return ri;
    }

    @ApiOperation(value = "批量上传文件到目录树", notes = "批量上传文件到Minion，并在目录树创建对应文件节点", response = Map.class, responseContainer = "List")
    @RequestMapping(value = "/{industryTypeRid}/files", method = RequestMethod.POST)
    public ReturnInfo uploadFilesAndAddFileNodes(@PathVariable String industryTypeRid,
                                                 @RequestParam("uploadingFiles") MultipartFile[] uploadingFiles,
                                                 @RequestParam String bucketName,
                                                 @RequestParam(defaultValue = "true") Boolean isOverride,
                                                 @RequestParam String metadata,
                                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException, EntityNotFoundException {
        log.info("uploadFilesAndAddFileNodesToIndustry(industryTypeRid={}, bucketName={}, isOverride={}, metadata={},tenantId={}, userId={})", industryTypeRid, bucketName, isOverride, objectMapper.writeValueAsString(metadata), tenantId, userId);
        List<Map<String, Object>> data = industryTypeService.uploadFilesAndAddFileNodesToIndustry(tenantId, userId, bucketName, industryTypeRid, isOverride, metadata, uploadingFiles);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), data);
        return ri;
    }

    @ApiOperation(value = "挂载对象类型和实例到分类", notes = "挂载对象类型和实例到分类", response = LinkObjectAndInstanceOutputBean.class, responseContainer = "List")
    @RequestMapping(value = "/{industryTypeRid}/link", method = RequestMethod.POST)
    public ReturnInfo linkObjectTypeAndInstance(@PathVariable String industryTypeRid,
                                                @RequestBody List<LinkObjectAndInstanceInputBean> objectInstances,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException, EntityNotFoundException {
        log.info("linkObjectTypeAndInstance(objectInstances={})", objectMapper.writeValueAsString(objectInstances));
        List<LinkObjectAndInstanceOutputBean> data = industryTypeService.updateLinkedObjectsAndInstances(tenantId, userId, industryTypeRid, objectInstances);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), data);
        return ri;
    }

    @ApiOperation(value = "名称是否可用", notes = "同分类下不可重名", response = Map.class)
    @RequestMapping(value = "/namesAvailable", method = RequestMethod.POST)
    public ReturnInfo namesAvailable(@RequestParam(required = false) String industryTypeRid,
                                     @RequestParam TreeNodeInputBean.TreeNodeTypeEnum childNodeType,
                                     @RequestBody List<String> names,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException, InputNotEnoughException {
        log.info("namesAvailable(names={})", objectMapper.writeValueAsString(names));
        Map<String, Boolean> data = industryTypeService.namesAvailable(tenantId, industryTypeRid, childNodeType, names);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, CodeAndMsg.E05000200.getMsg(), data);
        return ri;
    }

}
