package com.glodon.pcop.cimsvc.controller.deprecate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.DouplicateNameException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputNotEnoughException;
import com.glodon.pcop.cimsvc.model.input.DeleteTreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.input.MoveNodesInputBean;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.input.UpdateTreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeMetadataOutputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeOutputBean;
import com.glodon.pcop.cimsvc.service.v2.ContentService;
import com.glodon.pcop.cimsvc.service.v2.IndustryTypesService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// @Api(tags = "v1--数据管理目录树")
// @RestController
@RequestMapping(value = "/abort/content")
public class DataManagerContentController {
    private static Logger log = LoggerFactory.getLogger(DataManagerContentController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContentService contentService;

    @Autowired
    private IndustryTypesService industryTypesService;

    @ApiOperation(value = "目录子节点", notes = "查询目录树指定节点的子节点，默认输出根节点", response = TreeNodeOutputBean.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.POST)
    public ReturnInfo listChildNode(@RequestBody(required = false) TreeNodeInputBean parentNode,
                                    @RequestParam(defaultValue = "1") Integer childLevel,
                                    @RequestHeader(name = "PCOP-USERID") String userId,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, EntityNotFoundException {
        log.info("listChildNode(parentNode={})", objectMapper.writeValueAsString(parentNode));
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, contentService.listChildNode(tenantId, parentNode));
        return ri;
    }

    /*@ApiOperation(value = "删除目录节点", notes = "删除指定的目录节点", response = Boolean.class)
    @RequestMapping(value = "/node/delete", method = RequestMethod.POST)
    public ReturnInfo deleteChildNode(@RequestBody(required = false) DeleteTreeNodeInputBean parentNode,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("deleteChildNode(parentNode={})", objectMapper.writeValueAsString(parentNode));
        Object result = contentService.deleteContentNode(tenantId, parentNode);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }*/

    @ApiOperation(value = "更新文件目录节点", notes = "更新文件目录节点元数据", response = Boolean.class)
    @RequestMapping(value = "/node/update", method = RequestMethod.POST)
    public ReturnInfo updateChildNode(@RequestBody UpdateTreeNodeInputBean nodeMetadata,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, DouplicateNameException {
        log.info("updateChildNode(nodeMetadata={})", objectMapper.writeValueAsString(nodeMetadata));
        Boolean flag;
        if (nodeMetadata != null && nodeMetadata.getMetaData() != null) {
            flag = contentService.updateNodeMetadata(tenantId, userId, nodeMetadata, nodeMetadata.getMetaData());
        } else {
            flag = false;
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), flag);
        return ri;
    }

    @ApiOperation(value = "查询目录节点元数据", notes = "查询目录节点元数据", response = TreeNodeMetadataOutputBean.class)
    @RequestMapping(value = "/node/metadata", method = RequestMethod.POST)
    public ReturnInfo nodeMetadata(@RequestBody TreeNodeInputBean node,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException, CimDataEngineInfoExploreException, DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        log.info("nodeMetadata(node={})", objectMapper.writeValueAsString(node));
        TreeNodeMetadataOutputBean outputBean = contentService.getNodeMetadata(tenantId, userId, node);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), outputBean);
        return ri;
    }

    @ApiOperation(value = "批量上传文件到目录树", notes = "批量上传文件到Minion，并在目录树创建对应文件节点", response = Map.class, responseContainer = "List")
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ReturnInfo uploadFilesAndAddFileNodes(@RequestParam String node,
                                                 @RequestParam("uploadingFiles") MultipartFile[] uploadingFiles,
                                                 @RequestParam String bucketName,
                                                 @RequestParam(defaultValue = "true") Boolean isOverride,
                                                 @RequestParam String metadata,
                                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException, EntityNotFoundException, CimDataEngineRuntimeException, InputNotEnoughException {
        log.info("uploadFilesAndAddFileNodesToIndustry(node={}, bucketName={}, isOverride={}, metadata={},tenantId={}, userId={})", node, bucketName, isOverride, objectMapper.writeValueAsString(metadata), tenantId, userId);
        List<Map<String, Object>> data = industryTypesService.uploadFilesAndAddFileNodes(tenantId, userId, bucketName, node, isOverride, metadata, uploadingFiles);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), data);
        return ri;
    }

    @ApiOperation(value = "关联分类到实例", notes = "关联分类到实例", response = Boolean.class)
    @RequestMapping(value = "/instance/link", method = RequestMethod.POST)
    public ReturnInfo linkIndustryToInstance(@RequestBody(required = false) TreeNodeInputBean parentNode,
                                             @RequestParam String industryRid,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException, DataServiceModelRuntimeException {
        log.info("linkIndustryToInstance(parentNode={}, indusrtyRid={})", objectMapper.writeValueAsString(parentNode), industryRid);
        Boolean data = industryTypesService.linkIndustryToInstance(parentNode, industryRid, tenantId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), data);
        return ri;
    }

    @ApiOperation(value = "移动目录节点", notes = "移动目录节点", response = Boolean.class)
    @RequestMapping(value = "/node/move", method = RequestMethod.POST)
    public ReturnInfo moveNode(@RequestParam Boolean isOverride,
                               @RequestBody MoveNodesInputBean moveNodes,
                               @RequestHeader(name = "PCOP-USERID") String userId,
                               @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("moveNode(isOverride={}, moveNodes={})", isOverride, objectMapper.writeValueAsString(moveNodes));
        Object result = contentService.moveNodes(tenantId, isOverride, moveNodes);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }

    @ApiOperation(value = "批量删除目录节点", notes = "批量删除指定的目录节点", response = Boolean.class)
    @PostMapping(value = "/node/delete")
    public ReturnInfo deleteChildNodeList(@RequestBody(required = false) List<DeleteTreeNodeInputBean> parentNodeList,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("deleteContentNodeList(parentNodeList={})", objectMapper.writeValueAsString(parentNodeList));
        Object result = contentService.deleteContentNodeList(tenantId, parentNodeList);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }

}
