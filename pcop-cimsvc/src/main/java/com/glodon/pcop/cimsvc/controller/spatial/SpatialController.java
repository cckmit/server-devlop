package com.glodon.pcop.cimsvc.controller.spatial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.GisServerErrorException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.gis.GeneralCompositeInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.spatial.BufferIntersectQueryInput;
import com.glodon.pcop.cimsvc.model.v2.CompositeInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.gis.SpatialAnalysisQueryInput;
import com.glodon.pcop.cimsvc.service.spatial.CompositeQueryService;
import com.glodon.pcop.cimsvc.service.spatial.GeneralCompositeQueryService;
import com.glodon.pcop.cimsvc.service.spatial.GisQueryService;
import com.glodon.pcop.cimsvc.service.spatial.SpatialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(tags = "空间查询")
@RestController
@RequestMapping(value = "/spatial")
public class SpatialController {
    private static final Logger log = LoggerFactory.getLogger(SpatialController.class);

    @Autowired
    private SpatialService spatialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompositeQueryService compositeQueryService;

    @Autowired
    private GeneralCompositeQueryService generalCompositeQueryService;

    @Autowired
    private GisQueryService gisQueryService;

    @ApiOperation(value = "缓冲区包含查询", notes = "查询缓冲区内包含指定对象类型的实例", response = String.class, responseContainer = "list")
    @RequestMapping(value = "/bufferContain", method = RequestMethod.POST)
    public ReturnInfo bufferContain(@RequestParam String objectTypeId,
                                    @RequestBody BufferIntersectQueryInput queryConditions,
                                    @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                    @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws InterruptedException {
        Long stDate = System.currentTimeMillis();
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();

        // List<String> data = spatialService.bufferContainAnalysis(objectTypeId, queryConditions);
        List<String> data = spatialService.bufferContainAnalysis(objectTypeId, queryConditions);
        log.info("used time: {}", (System.currentTimeMillis() - stDate));
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }


    @ApiOperation(value = "上传shp文件并导入", notes = "上传shp文件并将文件数据导入CIM，zip压缩，只包含单个shp", response = Boolean.class)
    @RequestMapping(value = "/shpImport", method = RequestMethod.POST)
    public ReturnInfo shpFileInport(@RequestParam String objectTypeId,
                                    @RequestParam MultipartFile shpFile) {
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        spatialService.shpLoader(objectTypeId, shpFile);
        ReturnInfo ri = new ReturnInfo(code, message, "success");
        return ri;
    }


    @ApiOperation(value = "POI圆形缓冲区查询", notes = "POI圆形缓冲区查询", response = Boolean.class)
    @RequestMapping(value = "/circleBuffer", method = RequestMethod.POST)
    public ReturnInfo circleBuffer(@RequestParam String centerPoint,
                                   @RequestParam(defaultValue = "500") Double radius,
                                   @RequestParam List<String> objectTypeIds) throws JsonProcessingException {
        log.info("circleBuffer(centerPoint={}, radius={},objectTypeIds={})", centerPoint, radius,
                objectMapper.writeValueAsString(objectTypeIds));
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        Map<String, List<Map<String, Object>>> data = spatialService.circleBufferBatch(centerPoint, radius,
                objectTypeIds);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "匹配导入shp文件", notes = "匹配导入shp文件", response = Boolean.class)
    @RequestMapping(value = "/mappingImport", method = RequestMethod.POST)
    public ReturnInfo mappingImport(@RequestParam String tenantId,
                                    @RequestParam String MappingInfo,
                                    @RequestParam MultipartFile shpFile) {
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        spatialService.mappingImport(tenantId, MappingInfo, shpFile);
        ReturnInfo ri = new ReturnInfo(code, message, "success");
        return ri;
    }

    @ApiOperation(value = "一体化查询", notes = "一体化查询", response = SingleQueryOutput.class)
    @RequestMapping(value = "/compositeQuery", method = RequestMethod.POST)
    public ReturnInfo bufferQuery(@RequestBody CompositeInstancesQueryInput queryConditions,
                                  @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                  @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws GisServerErrorException, InputErrorException {
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        List<SingleQueryOutput> data = compositeQueryService.compositeQueryGis(tenantId, queryConditions);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "一体化查询，基于属性集", notes = "一体化查询，基于属性集", response = SingleQueryOutput.class)
    @RequestMapping(value = "/datasetBaseCompositeQuery", method = RequestMethod.POST)
    public ReturnInfo bufferQueryBaseDataset(@RequestBody GeneralCompositeInstancesQueryInput queryConditions,
                                             @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                             @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws GisServerErrorException, InputErrorException {
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        List<SingleQueryOutput> data = generalCompositeQueryService.compositeQueryGis(tenantId, queryConditions);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "空间分析", notes = "空间分析", response = SingleQueryOutput.class, responseContainer = "map")
    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    public ReturnInfo spatialAnalysis(@RequestBody @Validated SpatialAnalysisQueryInput queryConditions,
                                      @RequestHeader(name = "PCOP-USERID") String userId,
                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws InputErrorException,
            GisServerErrorException {
        log.info("spatialAnalysis(tenantId={}, queryConditions={})", tenantId, queryConditions);
        EnumWrapper.CodeAndMsg code = EnumWrapper.CodeAndMsg.E05000200;
        String message = EnumWrapper.CodeAndMsg.E05000200.getMsg();
        Map<String, SingleQueryOutput> data = gisQueryService.compositeQueryGis(tenantId, queryConditions);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

}
