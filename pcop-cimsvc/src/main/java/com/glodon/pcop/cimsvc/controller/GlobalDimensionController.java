package com.glodon.pcop.cimsvc.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.dimension.DimensionItemsOutput;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeInputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeOutputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeWithDatasetOutputBean;
import com.glodon.pcop.cimsvc.service.GlobalDimensionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
@Api(tags = "字典配置项")
@RestController
public class GlobalDimensionController {
    private static Logger log = LoggerFactory.getLogger(GlobalDimensionController.class);

    @Autowired
    private GlobalDimensionService globalDimensionService;

    @ApiOperation(value = "添加字典类型定义", notes = "添加字典类型定义", response = Boolean.class)
    @RequestMapping(value = {"/globalDimensionType", "/dictionaryType"}, method = RequestMethod.POST)
    public ReturnInfo addGlobalDimensionType(@RequestBody DimensionTypeInputBean dimensionType,
                                             @RequestHeader(name = "PCOP-USERID") String creator,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("addGlobalDimensionType(name={})", dimensionType.getDimensionTypeName());
        boolean flag = globalDimensionService.addConfigurationDimension(tenantId, dimensionType);
        if (flag) {
            return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), true);
        } else {
            return new ReturnInfo(EnumWrapper.CodeAndMsg.E05070001, EnumWrapper.CodeAndMsg.E05070001.getMsg(), false);
        }
    }

    @ApiOperation(value = "查询字典类型定义", notes = "查询字典类型定义", response = DimensionTypeWithDatasetOutputBean.class)
    @RequestMapping(value = {"/dictionaryType/{typeName}"}, method = RequestMethod.GET)
    public ReturnInfo listGlobalDimensionTypeWithDataset(@PathVariable String typeName,
                                                         @RequestHeader(name = "PCOP-USERID") String creator,
                                                         @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("listGlobalDimensionTypeWithDataset(typeName={})", typeName);
        DimensionTypeWithDatasetOutputBean dimensionType = globalDimensionService.getDimensionType(typeName);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, dimensionType);
    }

    @ApiOperation(value = "删除字典类型定义", notes = "删除字典类型定义", response = Boolean.class)
    @RequestMapping(value = {"/dictionaryType/{typeName}"}, method = RequestMethod.DELETE)
    public ReturnInfo deleteDimensionType(@PathVariable String typeName,
                                          @RequestHeader(name = "PCOP-USERID") String creator,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteDimensionType(typeName={})", typeName);
        boolean removeResult = globalDimensionService.removeDimensionType(typeName);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, removeResult);
    }

    @ApiOperation(value = "字典类型定义列表", notes = "字典类型定义列表", response = DimensionTypeOutputBean.class, responseContainer = "list")
    @RequestMapping(value = {"/dictionaryTypes"}, method = RequestMethod.GET)
    public ReturnInfo listGlobalDimensionTypes(@RequestParam(required = false) String keyWord,
                                               @RequestParam(defaultValue = "0") int pageIndex,
                                               @RequestParam(defaultValue = "50") int pageSize,
                                               @RequestHeader(name = "PCOP-USERID") String creator,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("listGlobalDimensionTypes(keyWord={}, pageIndex={}, pageSize={})", keyWord, pageIndex, pageSize);
        List<DimensionTypeOutputBean> dimensionTypes = globalDimensionService.listGlobalDimensions(keyWord, pageIndex,
                pageSize);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, dimensionTypes);
    }

    @ApiOperation(value = "添加字典数据项", notes = "添加字典数据项", response = Boolean.class)
    @RequestMapping(value = {"/globalDimensionType/{dimensionTypeName}", "/dictionaryType/{typeName}/item"}, method = RequestMethod.POST)
    public ReturnInfo addGlobalDimensionItem(@PathVariable String dimensionTypeName,
                                             @RequestBody Map<String, Object> item,
                                             @RequestHeader(name = "PCOP-USERID") String creator,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("addGlobalDimensionItem(dimensionTypeName={})", dimensionTypeName);
        boolean flag = globalDimensionService.addGlobalDimensionItem(dimensionTypeName, item);
        if (flag) {
            return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), true);
        } else {
            return new ReturnInfo(EnumWrapper.CodeAndMsg.E05070001, EnumWrapper.CodeAndMsg.E05070001.getMsg(), false);
        }
    }

    @ApiOperation(value = "查询字典所有数据项", notes = "查询字典所有数据项", response = HashMap.class, responseContainer = "list")
    @RequestMapping(value = {"/globalDimensionType/{typeName}", "/dictionaryType/{typeName}/items"}, method = RequestMethod.GET)
    public ReturnInfo listGlobalDimensionItems(@PathVariable String typeName,
                                               @RequestParam(required = false) String sortProperty,
                                               @RequestParam(required = false) ExploreParameters.SortingLogic sortingLogic,
                                               @RequestHeader(name = "PCOP-USERID") String creator,
                                               @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("listGlobalDimensionItems(typeName={}, sortProperty={}, sortingLogic={})", typeName, sortProperty, sortingLogic);
        List<Map<String, Object>> items = globalDimensionService.getGlobalDimensionItems(typeName, sortProperty, sortingLogic);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), items);
    }

    @ApiOperation(value = "删除字典数据项", notes = "删除字典数据项", response = boolean.class)
    @RequestMapping(value = "/dictionaryType/{typeName}/item/{itemRid}", method = RequestMethod.DELETE)
    public ReturnInfo deleteDimensionItem(@PathVariable String typeName,
                                          @PathVariable String itemRid,
                                          @RequestHeader(name = "PCOP-USERID") String creator,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteDimensionItem(typeName={}, itemRid={})", typeName, itemRid);
        boolean removeDimensionItem = globalDimensionService.removeDimensionItem(typeName, itemRid);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, removeDimensionItem);
    }

    @ApiOperation(value = "批量查询配置项", notes = "查询指定类型的所有配置项", response = DimensionItemsOutput.class, responseContainer = "list")
    @RequestMapping(value = {"/globalDimensionType/batch", "/dictionaryType/batch"}, method = RequestMethod.POST)
    public ReturnInfo listGlobalDimensionItemsBatch(@RequestBody List<String> dimensionTypeNames,
                                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("listGlobalDimensionItemsBatch(dimensionTypeNames={})", JSON.toJSON(dimensionTypeNames));
        List<DimensionItemsOutput> items = globalDimensionService.getGlobalDimensionItems(dimensionTypeNames);
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, items);
    }

}
