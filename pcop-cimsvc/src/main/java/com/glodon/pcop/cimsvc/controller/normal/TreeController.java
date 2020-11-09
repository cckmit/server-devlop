package com.glodon.pcop.cimsvc.controller.normal;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.tree.*;
import com.glodon.pcop.cimsvc.model.vo.InfoObjectTypeDetailVO;
import com.glodon.pcop.cimsvc.service.tree.*;
import com.glodon.pcop.cimsvc.service.v2.ContentService;
import com.glodon.pcop.cimsvc.service.v2.IndustryTypesService;
import com.glodon.pcop.cimsvc.util.mockdata.FirstLevelChildNodeCountMock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "树")
@RestController
@RequestMapping(value = "/tree")
public class TreeController {
    private static Logger log = LoggerFactory.getLogger(TreeController.class);

    @Autowired
    private InfoObjectTypeManagementService infoObjectTypeManagementService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private IndustryTypesService industryTypesService;

    @Autowired
    private TreeService treeService;

    @Autowired
    private TreeServiceWithPermission serviceWithPermission;

    @Autowired
    private TreeNodeService treeNodeService;

    @Autowired
    private DataPermissionService dataPermissionService;

    @Autowired
    private TreeDisplayNameExportService treeDisplayNameExportService;

    @Autowired
    private TreeServiceUtil treeServiceUtil;

    @ApiOperation(value = "展开树节点", notes = "列出指定树节点的子节点", response = NodeInfoBean.class, responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/expandTreeNode", method = RequestMethod.POST)
    public ReturnInfo expandTreeNode(@PathVariable String treeDefId,
                                     @RequestParam(defaultValue = "false") Boolean isCountChild,
                                     @RequestParam(defaultValue = "false") Boolean filterByPermission,
                                     @RequestParam(required = false) String permissionName,
                                     @RequestBody(required = false) NodeInfoBean parentNodeInfo,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("expandTreeNode(treeDefId={}, isCountChild={},filterByPermission={} , parentNode={})", treeDefId,
                isCountChild, filterByPermission, parentNodeInfo);
        List<NodeInfoBean> childNodes = serviceWithPermission.expandNodes(tenantId, userId, permissionName, treeDefId
                , parentNodeInfo, isCountChild, filterByPermission);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

    @ApiOperation(value = "子节点统计", notes = "第一层节点的所有子节点统计，子节点分为文件、非文件两类", response = ChildNodeCountBean.class,
            responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/childNodeCount", method = RequestMethod.POST)
    public ReturnInfo childNodeCount(@PathVariable String treeDefId,
                                     @RequestBody(required = false) NodeInfoBean parentNodeInfo,
                                     @RequestParam(defaultValue = "false") Boolean filterByPermission,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("childNodeCount(treeDefId={}, parentNode={})", treeDefId, parentNodeInfo);
        List<ChildNodeCountBean> childNodes;
        if (tenantId.equals("3")) {
            childNodes = FirstLevelChildNodeCountMock.childNodeCount();
        } else {
            childNodes = treeService.childNodeCount(tenantId, treeDefId, parentNodeInfo);
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

    @ApiOperation(value = "移动树节点", notes = "树节点移动", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/moveTreeNode", method = RequestMethod.POST)
    public ReturnInfo moveTreeNode(@PathVariable String treeDefId,
                                   @RequestParam(defaultValue = "true") Boolean isOverride,
                                   @RequestParam(defaultValue = "false") Boolean moveFlag,
                                   @RequestBody(required = false) NodeMoveInputBean moveInfo,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("moveTreeNode(treeDefId={},isOverride={}, sourceNodeInfo={}, moveInfo={})", treeDefId, isOverride,
                moveInfo);
        Boolean data = serviceWithPermission.moveNode(tenantId, userId, treeDefId, isOverride, moveFlag, moveInfo);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "删除树节点", notes = "删除树节点", response = NodeDeleteOutputBean.class, responseContainer = "list")
    @RequestMapping(value = "{treeDefId}/deleteTreeNode", method = RequestMethod.POST)
    public ReturnInfo deleteTreeNode(@PathVariable String treeDefId,
                                     @RequestParam(defaultValue = "false") Boolean recursive,
                                     @RequestParam(defaultValue = "false") Boolean filterByPermission,
                                     @RequestBody List<NodeInfoBean> nodeInfoList,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteTreeNode(treeDefId={}, recursive={}, filterByPermission={}, nodeInfo={})", treeDefId,
                recursive, filterByPermission, ArrayUtils.toString(nodeInfoList));
        List<NodeDeleteOutputBean> data = serviceWithPermission.deleteNodes(tenantId, userId, treeDefId, recursive,
                nodeInfoList, filterByPermission);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "更新节点显示名称", notes = "更新节点显示名称", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/updateTreeNodeName", method = RequestMethod.POST)
    public ReturnInfo updateTreeNodeDisplayName(@PathVariable String treeDefId,
                                                @RequestParam(defaultValue = "false") Boolean override,
                                                @RequestParam String nodeName,
                                                @RequestBody NodeInfoBean nodeInfo,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("updateTreeNodeDisplayName(treeDefId={}, override={}, nodeName={}, nodeInfo={})", treeDefId,
                override, nodeName, nodeInfo);
        Boolean data = serviceWithPermission.updateDisplayName(tenantId, userId, treeDefId, override, nodeName,
                nodeInfo);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "检索树节点", notes = "根据输入的关键词对树节点的名称进行检索", response = NodeInfoBean.class, responseContainer =
            "List")
    @RequestMapping(value = "{treeDefId}/searchTreeNode", method = RequestMethod.GET)
    public ReturnInfo searchTreeNode(@PathVariable String treeDefId,
                                     @RequestParam(defaultValue = "false") Boolean filterByPermission,
                                     @RequestParam String keyWord,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("searchTreeNode(treeDefId={}, tenantId={}, keyWord={})", treeDefId, tenantId, keyWord);
        List<NodeInfoBean> data = treeService.searchNodeByName(tenantId, userId, treeDefId, keyWord,
                filterByPermission);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "新增行业分类树节点", notes = "新增行业分类树节点", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/addIndustryNode", method = RequestMethod.POST)
    public ReturnInfo addIndustryNode(@PathVariable String treeDefId,
                                      @RequestParam(defaultValue = "false") Boolean dataPermission,
                                      @RequestBody(required = false) IndustryNodeAddInputBean nodeInfo,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws
            DataServiceUserException,
            CimDataEngineRuntimeException,
            DataServiceModelRuntimeException,
            CimDataEngineInfoExploreException {
        log.info("addIndustryNode(treeDefId={}, dataPermission={}, nodeInfo={})", treeDefId, dataPermission, nodeInfo);
        String industryRid = serviceWithPermission.addIndustryNode(tenantId, userId, treeDefId, nodeInfo,
                dataPermission);
        ReturnInfo ri;
        if (StringUtils.isNotBlank(industryRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05090001, EnumWrapper.CodeAndMsg.E05090001.getMsg(), false);
        }
        return ri;
    }

    @ApiOperation(value = "新增实例树节点", notes = "新增实例树节点", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/addInstanceNode", method = RequestMethod.POST)
    public ReturnInfo addInstanceNode(@PathVariable String treeDefId,
                                      @RequestParam String objectTypeId,
                                      @RequestParam(defaultValue = "true") Boolean dataPermission,
                                      @RequestBody(required = false) IndustryNodeAddInputBean nodeInfo,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws
            DataServiceUserException,
            CimDataEngineRuntimeException {
        log.info("addInstanceNode(treeDefId={}, objectTypeId={}, dataPermission={}, nodeInfo={})", treeDefId,
                objectTypeId, dataPermission, nodeInfo);
        String industryRid = serviceWithPermission.addInstanceNode(tenantId, userId, treeDefId, objectTypeId, nodeInfo,
                dataPermission);
        ReturnInfo ri;
        if (StringUtils.isNotBlank(industryRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05090001, EnumWrapper.CodeAndMsg.E05090001.getMsg(), false);
        }
        return ri;
    }

    @ApiOperation(value = "挂载对象类型和实例到分类", notes = "挂载对象类型和实例到分类", response = Map.class)
    @RequestMapping(value = "{treeDefId}/linkObjectTypeAndInstance", method = RequestMethod.POST)
    public ReturnInfo linkObjectTypeAndInstance(@PathVariable String treeDefId,
                                                @RequestBody LinkObjectTypeAndInstanceInputBean nodeInfo,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("linkObjectTypeAndInstance(treeDefId={}, nodeInfo={})", treeDefId, nodeInfo);
        Map<String, Boolean> data = treeNodeService.linkObjectAndInstance(tenantId, treeDefId, userId, nodeInfo);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "新增对象类型树节点", notes = "新增对象类型树节点", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/addObjectTypeNode", method = RequestMethod.POST)
    public ReturnInfo addObjectTypeNode(@PathVariable String treeDefId,
                                        @RequestParam(defaultValue = "false") Boolean dataPermission,
                                        @RequestBody ObjectTypeAddInputBean nodeInfo,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        String objectTypeRid = treeService.addObjectTypeNode(tenantId, userId, treeDefId, nodeInfo, dataPermission);

        ReturnInfo ri;
        if (StringUtils.isNotBlank(objectTypeRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05090001, EnumWrapper.CodeAndMsg.E05090001.getMsg(), false);
        }
        return ri;

    }


    @ApiOperation(value = "新增属性集树节点", notes = "新增属性集树节点", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/addDatasetNode", method = RequestMethod.POST)
    public ReturnInfo addObjectTypeNode(@PathVariable String treeDefId,
                                        @RequestParam(defaultValue = "false") Boolean dataPermission,
                                        @RequestBody DatasetAddInputBean nodeInfo,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        String objectTypeRid = treeService.addDatasetNode(tenantId, userId, treeDefId, nodeInfo, dataPermission);

        ReturnInfo ri;
        if (StringUtils.isNotBlank(objectTypeRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05090001, EnumWrapper.CodeAndMsg.E05090001.getMsg(), false);
        }
        return ri;

    }

    @ApiOperation(value = "新增关系类型树节点", notes = "新增关系类型树节点", response = Boolean.class)
    @RequestMapping(value = "{treeDefId}/addRelationShipNode", method = RequestMethod.POST)
    public ReturnInfo addRelationShipNode(@PathVariable String treeDefId,
                                        @RequestParam(defaultValue = "false") Boolean dataPermission,
                                        @RequestBody RelationShipAddInputBean nodeInfo,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        String objectTypeRid = treeService.addRelationShipNode(tenantId, userId, treeDefId, nodeInfo, dataPermission);

        ReturnInfo ri;
        if (StringUtils.isNotBlank(objectTypeRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05090001, EnumWrapper.CodeAndMsg.E05090001.getMsg(), false);
        }
        return ri;

    }




    @ApiOperation(value = "批量上传文件到目录树", notes = "批量上传文件到Minion，并在目录树创建对应文件节点", response = Map.class,
            responseContainer = "List")
    @PostMapping(value = "{treeDefId}/uploadFiles")
    public ReturnInfo uploadFiles(@PathVariable String treeDefId,
                                  @RequestParam String parentNode,
                                  @RequestParam("uploadingFiles") MultipartFile[] uploadingFiles,
                                  @RequestParam String bucketName,
                                  @RequestParam(defaultValue = "true") Boolean isOverride,
                                  @RequestParam String metadata,
                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException {
        log.info("uploadFiles(treeDefId={}, targetNodeInfo={}, bucketName={}, isOverride={}, metadata={},tenantId={}," +
                " userId={})", treeDefId, parentNode, bucketName, isOverride, metadata, tenantId, userId);
        List<Map<String, Object>> data = industryTypesService.uploadFileAndAddNode(tenantId, userId, treeDefId,
                bucketName, isOverride, parentNode, metadata, uploadingFiles);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "树节点元数据", notes = "树节点元数据", response = NodeMetadataOutputBean.class)
    @PostMapping(value = "{treeDefId}/metadataInfo")
    public ReturnInfo getMetadataInfo(@PathVariable String treeDefId,
                                      @RequestParam(defaultValue = "false") Boolean includePermission,
                                      @RequestParam(required = false) String permissionName,
                                      @RequestBody NodeInfoBean nodeInfo,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException {
        log.info("getMetadataInfo(treeDefId={},includePermission={}, permissionName={},nodeInfo={})", treeDefId,
                includePermission, permissionName, nodeInfo);
        NodeMetadataOutputBean outputBean = serviceWithPermission.getTreeNodeMetadata(tenantId, userId, treeDefId,
                includePermission, permissionName, nodeInfo);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                outputBean);
        return ri;
    }

    @ApiOperation(value = "更新树节点元数据", notes = "更新树节点元数据", response = Boolean.class)
    @PostMapping(value = "{treeDefId}/updateMetadataInfo")
    public ReturnInfo updateMetadataInfo(@PathVariable String treeDefId,
                                         @RequestBody NodeMetadataUpdateInputBean nodeMetadataInfo,
                                         @RequestHeader(name = "PCOP-USERID") String userId,
                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("updateMetadataInfo(treeDefId={}, nodeMetadataInfo={})", treeDefId, nodeMetadataInfo);
        Boolean data = serviceWithPermission.updateMetadataInfo(tenantId, userId, treeDefId, nodeMetadataInfo);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    @ApiOperation(value = "查询节点权限", notes = "查询节点权限，用户对该节点具有的权限", response = DataPermissionBean.class,
            responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/dataPermission", method = RequestMethod.POST)
    public ReturnInfo queryDataPermissionByUser(@PathVariable String treeDefId,
                                                @RequestBody List<String> nodeIds,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("queryDataPermissionByUser(treeDefId={}, userId={}, nodeIds={})", treeDefId, userId,
                StringUtils.join(nodeIds, ','));
        Assert.hasText(userId, "userId is mandatory");
        List<DataPermissionBean> childNodes = dataPermissionService.queryDataPermissionByUser(treeDefId, userId,
                nodeIds);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

    @ApiOperation(value = "查询子节点", notes = "根据指定的层数查询子节点", response = NodeInfoBean.class, responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/listChildNodes", method = RequestMethod.POST)
    public ReturnInfo listChildNodes(@PathVariable String treeDefId,
                                     @RequestParam(defaultValue = "3") Integer loopCount,
                                     @RequestParam(defaultValue = "false") Boolean filterByPermission,
                                     @RequestParam(required = false) String permissionName,
                                     @RequestBody(required = false) NodeInfoBean parentNodeInfo,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("listChildNodes(treeDefId={}, loopCount={}, filterByPermission={}, permissionName={}, parentNode={})"
                , treeDefId, loopCount, filterByPermission, permissionName, parentNodeInfo);
        List<NodeInfoBean> childNodes = serviceWithPermission.listChildNodesRecursively(tenantId, userId,
                permissionName, treeDefId, parentNodeInfo, loopCount, filterByPermission);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

    @ApiOperation(value = "导出节点显示名称", notes = "导出节点显示名称，若指定父节点节点，则导出该节点及其所有子节点，若未指定父节点，则导出所有节点", response =
            NodeInfoBean.class, responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/export", method = RequestMethod.POST)
    public void exportDisplayNames(@PathVariable String treeDefId,
                                   @RequestParam(required = false) String treeName,
                                   @RequestBody(required = false) NodeInfoBean parentNodeInfo,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                   HttpServletResponse response) throws IOException, CimDataEngineRuntimeException {
        log.info("listChildNodes(treeDefId={}, parentNode={})", treeDefId, parentNodeInfo);
        treeDisplayNameExportService.treeNodesExport(response, tenantId, userId, treeDefId, parentNodeInfo, treeName);
    }

    @ApiOperation(value = "新增场景树节点", notes = "新增场景树节点", response = Boolean.class)
    @RequestMapping(value = "/sceneTree", method = RequestMethod.POST)
    public ReturnInfo addSceneTreeNode(@RequestBody(required = false) SceneTreeNodeAddInputBean nodeInfo,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws CimDataEngineRuntimeException, DataServiceModelRuntimeException, DataServiceUserException {
        log.info("addSceneTreeNode(nodeInfo={})", nodeInfo);
        String industryRid = treeService.addSceneTreeNode(tenantId, userId, nodeInfo);
        ReturnInfo ri;
        if (StringUtils.isNotBlank(industryRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    false);
        }
        return ri;
    }

    @ApiOperation(value = "新增场景树节点不包含权限", notes = "新增场景树节点不包含权限", response = Boolean.class)
    @RequestMapping(value = "/addSceneTreeWithoutPermission", method = RequestMethod.POST)
    public ReturnInfo addSceneTreeNodeWithoutPermission(@RequestBody(required = false) SceneTreeNodeAddInputBean nodeInfo,
                                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws CimDataEngineRuntimeException, DataServiceModelRuntimeException, DataServiceUserException {
        log.info("addSceneTreeNode(nodeInfo={})", nodeInfo);
        String industryRid = treeService.addSceneTreeNodeWithoutPermission(tenantId, userId, nodeInfo);
        ReturnInfo ri;
        if (StringUtils.isNotBlank(industryRid)) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                    false);
        }
        return ri;
    }

    @ApiOperation(value = "根据查询条件挂载对象类型和实例到分类", notes = "根据查询条件挂载对象类型和实例到分类", response = Integer.class)
    @RequestMapping(value = "{treeDefId}/linkObjectTypeAndInstance/byQuery", method = RequestMethod.POST)
    public ReturnInfo linkObjectTypeAndInstanceByQuery(@PathVariable String treeDefId,
                                                       @RequestBody LinkObjectAndInstancesByQueryInputBean linkInput,
                                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws InputErrorException, EntityNotFoundException {
        log.info("linkObjectTypeAndInstanceByQuery(treeDefId={}, linkInput={}, tenantId={}, userId={})", treeDefId,
                linkInput, tenantId, userId);
        int data = treeNodeService.linkObjectAndInstanceByQuery(tenantId, treeDefId, userId, linkInput);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }

    // added by wayne 20190716
    @ApiOperation(value = "批量文件到目录树", notes = "批量处理Minion上的文件在目录树创建对应文件节点", response = Map.class,
            responseContainer = "List")
    @PostMapping(value = "{treeDefId}/fileinfos")
    public ReturnInfo fileinfos(@PathVariable String treeDefId,
                                @RequestParam String parentNode,
                                @RequestParam String bucketName,
                                @RequestParam(defaultValue = "true") Boolean isOverride,
                                @RequestParam String metadata,
                                @RequestHeader(name = "PCOP-USERID") String userId,
                                @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws IOException {
        log.info("fileinfos(treeDefId={}, targetNodeInfo={}, bucketName={}, isOverride={}, metadata={},tenantId={}," +
                " userId={})", treeDefId, parentNode, bucketName, isOverride, metadata, tenantId, userId);
        List<Map<String, Object>> data = industryTypesService.fileInfoAndAddNode(tenantId, userId, treeDefId,
                bucketName, isOverride, parentNode, metadata);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                data);
        return ri;
    }


    @ApiOperation(value = "建立系统默认数据内容", notes = "为新建租户建立系统默认数据内容")
    @PostMapping(value = "/addDefaultDataContext")
    public ReturnInfo addDefaultDataContext(
            @RequestParam String userId,
            @RequestParam String tenantId
    ) {
        log.info("addDefaultDataContext(userId={}, tenantId={}", userId, tenantId);
        Map<String, Boolean> strBooleanMap = treeService.addDefaultDataContextV2(tenantId, userId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                strBooleanMap);

        return ri;
    }

    @ApiOperation(value = "复制指定节点及其子节点", notes = "复制指定节点及其子节点", response = Boolean.class)
    @PostMapping(value = "{treeDefId}/copyTreeNode")
    public ReturnInfo copyNodeRecursively(
            @PathVariable String treeDefId,
            @RequestBody NodeCopyInputBean nodeCopyInput,
            @RequestHeader(name = "PCOP-USERID") String userId,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("copyNodeRecursively(userId={}, tenantId={}, nodeCopyInput={})", userId, tenantId, nodeCopyInput);
        boolean flag = treeServiceUtil.nodeCopyRecursiveWithPermission(treeDefId, tenantId, userId, nodeCopyInput);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                flag);
        return ri;
    }

    @ApiOperation(value = "对象树", notes = "对象树", response = InfoObjectTypeDetailVO.class)
    @PostMapping(value = "/queryInfoObjectTypeDetails")
    public ReturnInfo queryInfoObjectTypeDetails(
            @RequestBody InfoObjectTypeDetailInputBean inputBean,
            @RequestHeader(name = "PCOP-USERID") String userId,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("queryInfoObjectTypeDetails(userId={}, tenantId={}, InfoObjectTypeDetailInputBean={})", userId, tenantId, inputBean);
        List<InfoObjectTypeDetailVO> result = infoObjectTypeManagementService.queryInfoObjectTypeDetails(tenantId, userId, inputBean);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return ri;
    }

}
