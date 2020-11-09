package com.glodon.pcop.cimsvc.controller.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.UIViewFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.UIViewVo;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.v2.DimensionQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.service.UIViewService;
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

@Api(tags = "UI界面数据查询VIEW")
@RestController
public class ViewController {
    private static Logger log = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "查询View定义", notes = "根据viewId，查询View定义", response = UIViewVo.class)
    @RequestMapping(value = "/view/{viewId}", method = RequestMethod.GET)
    public ReturnInfo getViewDef(@PathVariable String viewId) {
        log.info("getViewDef(viewId={})", viewId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                UIViewFeatures.getUIViewById(CimConstants.defauleSpaceName, viewId));
        return ri;
    }

    @ApiOperation(value = "删除View定义", notes = "根据viewId，删除View定义", response = Boolean.class)
    @RequestMapping(value = "/view/{viewId}", method = RequestMethod.DELETE)
    public ReturnInfo deleteViewDef(@PathVariable String viewId) {
        log.info("deleteViewDef(viewId={})", viewId);
        boolean flag = UIViewFeatures.deleteUIView(viewId, CimConstants.defauleSpaceName);
        ReturnInfo ri;
        if (flag) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040402, false);
        }
        return ri;
    }

    @ApiOperation(value = "新增View定义", notes = "新增View定义，viewId唯一", response = Boolean.class)
    @RequestMapping(value = "/view", method = RequestMethod.POST)
    public ReturnInfo addViewDef(@RequestBody UIViewVo view) throws JsonProcessingException {
        log.info("addViewDef(view={})", objectMapper.writeValueAsString(view));
        boolean flag = UIViewFeatures.addUIView(view, CimConstants.defauleSpaceName);
        ReturnInfo ri;
        if (flag) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040406, false);
        }
        return ri;
    }

    @ApiOperation(value = "更新View定义", notes = "根据viewId，更新View定义", response = Boolean.class)
    @RequestMapping(value = "/view/{viewId}", method = RequestMethod.POST)
    public ReturnInfo updateViewDef(@PathVariable String viewId,
                                    @RequestBody UIViewVo view) throws JsonProcessingException {
        log.info("updateViewDef(viewId={}, view={})", viewId, objectMapper.writeValueAsString(view));
        boolean flag = UIViewFeatures.updateUIView(viewId, view, CimConstants.defauleSpaceName);
        ReturnInfo ri;
        if (flag) {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, true);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040403, false);
        }
        return ri;
    }

    @ApiOperation(value = "View批量查询", notes = "根据objectTypeId批量查询View定义", response = List.class)
    @RequestMapping(value = "/view/infoObject/{objectTypeId}", method = RequestMethod.GET)
    public ReturnInfo queryViewDef(@PathVariable String objectTypeId,
                                   @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        log.info("queryViewDef(objectTypeId={})", objectTypeId);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                UIViewService.queryUIViewByObjectType(objectTypeId));
        return ri;
    }

    @ApiOperation(value = "View查询", notes = "查询View定义", response = DimensionQueryOutput.class)
    @RequestMapping(value = "/view/batch", method = RequestMethod.POST)
    public ReturnInfo queryViewBatch(@RequestBody InstancesQueryInput conditions,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("queryViewBatch(conditions={})", conditions);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                UIViewService.queryUIView(conditions));
        return ri;
    }
}
