package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionAddInputBean;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.service.tree.DataPermissionService;
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

import java.util.List;
import java.util.Map;

@Api(tags = "数据权限方案")
@RestController
@RequestMapping(value = "/tree/")
public class PermissionSchemaController {
    private static Logger log = LoggerFactory.getLogger(PermissionSchemaController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataPermissionService dataPermissionService;

    @ApiOperation(value = "添加节点权限", notes = "添加节点权限", response = String.class, responseContainer = "Map")
    @RequestMapping(value = "{treeDefId}/permissionSchema/{permissionSchemaId}/addDataPermission", method =
            RequestMethod.POST)
    public ReturnInfo addDataPermission(@PathVariable String treeDefId,
                                        @PathVariable String permissionSchemaId,
                                        @RequestBody List<DataPermissionAddInputBean> dataPermissions,
                                        @RequestHeader(name = "PCOP-USERID") String userId,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("addDataPermission(treeDefId={}, permissionSchemaId={}, dataPermissions={})", treeDefId,
                permissionSchemaId, objectMapper.writeValueAsString(dataPermissions));
        Map<String, Boolean> data = dataPermissionService.addDataPermission(treeDefId, permissionSchemaId,
                dataPermissions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "更新节点权限", notes = "更新节点权限", response = String.class, responseContainer = "Map")
    @RequestMapping(value = "{treeDefId}/permissionSchema/{permissionSchemaId}/updateDataPermission", method =
            RequestMethod.POST)
    public ReturnInfo updateDataPermission(@PathVariable String treeDefId,
                                           @PathVariable String permissionSchemaId,
                                           @RequestBody List<DataPermissionBean> dataPermissions,
                                           @RequestHeader(name = "PCOP-USERID") String userId,
                                           @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("addDataPermission(treeDefId={}, permissionSchemaId={}, dataPermissions={})", treeDefId,
                permissionSchemaId, objectMapper.writeValueAsString(dataPermissions));

        Map<String, Boolean> data = dataPermissionService.updateDataPermission(treeDefId, permissionSchemaId, dataPermissions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询节点权限", notes = "查询节点权限", response = DataPermissionBean.class,
            responseContainer = "List")
    @RequestMapping(value = "{treeDefId}/permissionSchema/{permissionSchemaId}/dataPermission", method =
            RequestMethod.POST)
    public ReturnInfo queryDataPermission(@PathVariable String treeDefId,
                                          @PathVariable String permissionSchemaId,
                                          @RequestBody List<String> nodeIds,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws
            JsonProcessingException {
        log.info("queryDataPermission(treeDefId={}, permissionSchemaId={}, nodeIds={})", treeDefId, permissionSchemaId,
                objectMapper.writeValueAsString(nodeIds));
        List<DataPermissionBean> childNodes = dataPermissionService.queryDataPermission(userId, treeDefId, permissionSchemaId,
                nodeIds);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

    @ApiOperation(value = "删除节点权限", notes = "删除节点权限", response = String.class, responseContainer = "Map")
    @RequestMapping(value = "{treeDefId}/permissionSchema/{permissionSchemaId}/deleteDataPermission", method =
            RequestMethod.POST)
    public ReturnInfo deleteDataPermission(@PathVariable String treeDefId,
                                           @PathVariable String permissionSchemaId,
                                           @RequestBody List<String> dataPermissionRids,
                                           @RequestHeader(name = "PCOP-USERID") String userId,
                                           @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("deleteDataPermission(treeDefId={}, permissionSchemaId={}, dataPermissions={})", treeDefId,
                permissionSchemaId, objectMapper.writeValueAsString(dataPermissionRids));

        Map<String, Boolean> data = dataPermissionService.deleteDataPermission(treeDefId, permissionSchemaId,
                dataPermissionRids);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

}
