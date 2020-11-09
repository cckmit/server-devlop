package com.glodon.pcop.cimsvc.controller.tag;


import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.tag.CommonTagAddInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagBaseInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.DataEngineException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.output.InstancesByTagOutputBean;
import com.glodon.pcop.cimsvc.service.tag.CommonTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"标签相关接口"})
@RestController
@RequestMapping(value = "/commonTag")
public class CommonTagController {
    private static final Logger log = LoggerFactory.getLogger(CommonTagController.class);

    @Autowired
    private CommonTagService commonTagService;

    @ApiOperation(value = "新增标签", notes = "新增标签", response = CommonTagOutputBean.class)
    @PostMapping
    public ReturnInfo addCommonTag(@RequestBody CommonTagAddInputBean tagAddInput,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException {
        log.info("addCommonTag(tagAddInput={})", tagAddInput);

        CommonTagOutputBean data = commonTagService.addCommonTag(tenantId, userId, tagAddInput);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询标签", notes = "查询标签", response = CommonTagOutputBean.class)
    @GetMapping(path = "/{tagName}")
    public ReturnInfo getCommonTag(@PathVariable String tagName,
                                   @RequestHeader(name = "PCOP-USERID") String userId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("getCommonTag(tannatId={}, tagName={})", tenantId, tagName);

        CommonTagOutputBean data = commonTagService.getCommonTag(tenantId, userId, tagName);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }


    @ApiOperation(value = "更新标签", notes = "更新标签", response = CommonTagOutputBean.class)
    @PutMapping(path = "/{tagName}")
    public ReturnInfo updateCommonTag(@PathVariable String tagName,
                                      @RequestBody CommonTagBaseInputBean tagUpdateInput,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("updateCommonTag(tennatId={}, tagName={}, tagUpdateInput={})", tenantId, tagName, tagUpdateInput);

        CommonTagOutputBean data = commonTagService.updateCommonTag(tenantId, userId, tagName, tagUpdateInput);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "删除标签", notes = "删除标签", response = Boolean.class)
    @DeleteMapping(path = "/{tagName}")
    public ReturnInfo deleteCommonTag(@PathVariable String tagName,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("deleteCommonTag(tennatId={}, tagName={})", tenantId, tagName);

        boolean data = commonTagService.deleteCommonTag(tenantId, userId, tagName);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询标签的子标签", notes = "查询标签的子标签", response = CommonTagOutputBean.class, responseContainer = "list")
    @GetMapping(path = "/{tagName}/childTags")
    public ReturnInfo getCommonTagChildTags(@PathVariable String tagName,
                                            @RequestHeader(name = "PCOP-USERID") String userId,
                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("getCommonTagChildTags(tenantId={}, tagName={})", tenantId, tagName);

        List<CommonTagOutputBean> data = commonTagService.getChildCommonTags(tenantId, userId, tagName);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询标签的子标签", notes = "查询标签的子标签", response = CommonTagOutputBean.class)
    @GetMapping(path = "/{tagName}/parentTags")
    public ReturnInfo getCommonTagParentTags(@PathVariable String tagName,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("getCommonTagParentTags(tenantId={}, tagName={})", tenantId, tagName);

        CommonTagOutputBean data = commonTagService.getParentCommonTag(tenantId, userId, tagName);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询对象类型关联的标签", notes = "查询对象类型关联的标签", response = CommonTagOutputBean.class)
    @GetMapping(path = "/{objectTypeId}/attachedObjectType")
    public ReturnInfo getCommonTagAttachedObjectType(@PathVariable String objectTypeId,
                                                     @RequestParam String relationType,
                                                     @RequestParam RelationDirection relationDirection,
                                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws CimDataEngineRuntimeException, DataEngineException, EntityNotFoundException {
        log.info("getCommonTagAttachedObjectType(tenantId={}, objectTypeId={}, relationType={}, relationDirection={})",
                tenantId, objectTypeId, relationType, relationDirection);

        List<CommonTagOutputBean> data = commonTagService.getCommonTagsByObjectType(tenantId, userId, objectTypeId,
                relationType, relationDirection);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询属性集关联的标签", notes = "查询属性集关联的标签", response = CommonTagOutputBean.class)
    @GetMapping(path = "/{dataSetRid}/attachedDataset")
    public ReturnInfo getCommonTagAttachedDataset(@PathVariable String dataSetRid,
                                                  @RequestParam String relationType,
                                                  @RequestParam RelationDirection relationDirection,
                                                  @RequestHeader(name = "PCOP-USERID") String userId,
                                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws DataEngineException, CimDataEngineRuntimeException {
        log.info("getCommonTagAttachedDataset(tenantId={}, dataSetRid={}, relationType={}, relationDirection={})",
                tenantId, dataSetRid, relationType, relationDirection);

        List<CommonTagOutputBean> data = commonTagService.getCommonTagsByDataSetRid(tenantId, userId, dataSetRid,
                relationType, relationDirection);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询标签关联的对象类型", notes = "查询标签关联的对象类型", response = ObjectTypeEntity.class, responseContainer =
            "list")
    @GetMapping(path = "/{tagName}/attachedObjectTypes")
    public ReturnInfo getObjectTypeByCommonTag(@PathVariable String tagName,
                                               @RequestParam String relationType,
                                               @RequestParam RelationDirection relationDirection,
                                               @RequestHeader(name = "PCOP-USERID") String userId,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("getObjectTypeByCommonTag(tenantId={}, tagName={}, relationType={}, relationDirection={})",
                tenantId, tagName, relationType, relationDirection);

        List<ObjectTypeEntity> data = commonTagService.getObjectTypesByCommonTag(tenantId, userId, tagName,
                relationType, relationDirection);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "查询标签关联的实例", notes = "查询标签关联的实例", response = InstancesByTagOutputBean.class, responseContainer = "list")
    @GetMapping(path = "/{tagName}/attachedInstances")
    public ReturnInfo getInstancesByCommonTag(@PathVariable String tagName,
                                              @RequestParam String relationType,
                                              @RequestParam RelationDirection relationDirection,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException {
        log.info("getInstancesByCommonTag(tenantId={}, tagName={}, relationType={}, relationDirection={})",
                tenantId, tagName, relationType, relationDirection);

        List<InstancesByTagOutputBean> data = commonTagService.getInstanceByCommonTag(tenantId, userId, tagName,
                relationType, relationDirection);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200, data);
        return ri;
    }

}
