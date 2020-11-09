package com.glodon.pcop.cimsvc.controller;

import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.ObjectTypeCache;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeStatusCacheVO;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerTypeBean;
import com.glodon.pcop.cimsvc.service.CimDataService;
import com.glodon.pcop.cimsvc.service.WorkerCountService;
import com.glodon.pcop.cimsvc.service.kafka.SendMessageUtil;
import com.glodon.pcop.cimsvc.service.tree.DataPermissionCache;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jimmy.Liu(liuzm @ glodon.com), Jul/07/2018.
 */
// @Api(value = "/", tags = "测试工具")
// @RestController
// @RequestMapping("/v1")
public class CimController {
    static Logger mLog = LoggerFactory.getLogger(CimController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CimDataService cimDataService;

    @Autowired
    private WorkerCountService wcs;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private DataPermissionCache dataPermissionCache;

    @ApiOperation(value = "About Me", notes = "About Pcop MajorPM")
    @RequestMapping("/hi")
    public String aboutMe() {
        Locale loc = LocaleContextHolder.getLocale();    //RequestContextUtils.getLocale(request);
        mLog.info("[Locale]: {}", loc);
        String sysName = messageSource.getMessage("msg.sys.name", null, loc);
        String started = messageSource.getMessage("msg.sys.started", null, loc);
        String msg = sysName + " " + started;
        mLog.info(msg);

        return msg;
    }


    @ApiOperation(value = "查询缓存对象", notes = "查询缓存对象")
    @RequestMapping(value = "/cachee/{cacheType}/{key}/obj", method = RequestMethod.POST)
    public InfoObjectTypeStatusCacheVO getCacheItemObject(@PathVariable(name = "key") String key,
                                                          @PathVariable(name = "cacheType") String cacheType) {
        String cacheName = CacheUtil.getCacheName(CimConstants.defauleSpaceName, cacheType);
        mLog.info("getListCacheItem(cacheName={}, key={})", cacheName, key);
        return ObjectTypeCache.getObjectStatusCacheItem(CimConstants.defauleSpaceName, key);
    }

    @ApiOperation(value = "查询数据权限缓存对象", notes = "查询数据权限缓存对象")
    @RequestMapping(value = "/cachee/{schemaId}/{nodeId}/obj", method = RequestMethod.GET)
    public DataPermissionBean getDataPermissionCacheItemObject(@PathVariable(name = "schemaId") String schemaId,
                                                               @PathVariable(name = "nodeId") String nodeId) {
        DataPermissionBean cacheName = dataPermissionCache.getDataPermissionByNodeRid(schemaId, nodeId);
        mLog.info("DataPermissionBean: [{}]", cacheName);
        return cacheName;
    }

    @ApiOperation(value = "测试", notes = "接口请求简单测试", response = WorkerPerTypeBean.class, responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType =
                    "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/test/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getWorkerCountPerType(@PathVariable String projectId,
                                            @RequestParam String name,
                                            @RequestHeader(name = "PCOP-USERID") String creator,
                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        mLog.info("getWorkerCountPerType(creator={}, tenantId={}, projectId={}, name={})", creator, tenantId,
                projectId, name);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                wcs.getWorkerCountPerWorkType());
        return ri;
    }

    @ApiOperation(value = "文件格式转换", notes = "office to pdf", response = WorkerPerTypeBean.class, responseContainer =
            "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buckte", value = "bucket name", required = true, dataType = "String",
                    paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "file name", required = true, dataType = "String",
                    paramType = "query")})
    @RequestMapping(value = "/test/converter", method = RequestMethod.GET)
    public ReturnInfo fileFormatTranslate(@RequestParam String buckte,
                                          @RequestParam String fileName) {
        mLog.info("fileFormatTranslate(buckte={}, fileName={})", buckte, fileName);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                wcs.getWorkerCountPerWorkType());
        return ri;
    }

    @ApiOperation(value = "kafka message test", notes = "kafka message test", response = WorkerPerTypeBean.class,
            responseContainer = "list")
    @RequestMapping(value = "/test/kafka", method = RequestMethod.GET)
    public ReturnInfo sendBimFileUploadTranslate(@RequestParam String buckte,
                                                 @RequestParam String fileName,
                                                 @RequestHeader(name = "PCOP-USERID") String creator,
                                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        mLog.info("sendBimFileUploadTranslate(buckte={}, fileName={})", buckte, fileName);
        sendMessageUtil.sendMessage(new BimFileUploadTranslateBean(buckte, fileName, null, fileName, tenantId));
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                wcs.getWorkerCountPerWorkType());
        return ri;
    }

}
