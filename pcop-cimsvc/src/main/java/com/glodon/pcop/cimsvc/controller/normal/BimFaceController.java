package com.glodon.pcop.cimsvc.controller.normal;

import com.bimface.sdk.bean.response.IntegrateBean;
import com.bimface.sdk.exception.BimfaceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.common.model.bim.BimfaceIntegrateInputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.BimfaceProcessingException;
import com.glodon.pcop.cimsvc.exception.BimfaceResponseErrorException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import com.glodon.pcop.cimsvc.service.bim.BimOutputBean;
import com.glodon.pcop.cimsvc.service.v2.BimFaceService;
import com.glodon.pcop.cim.common.util.bimface.BimFaceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@Api(tags = {"BIMFace服务"})
@RestController
@RequestMapping(value = "/bimface")
public class BimFaceController {
    private static Logger log = LoggerFactory.getLogger(BimFaceController.class);

    @Autowired
    private BimFaceService bimFaceService;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "获取view token", response = String.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "bimfaceId", value = "对象模型ID", example = "123456", required = true,
            dataType = "Long", paramType = "query")})
    @RequestMapping(value = "/viewToken", method = RequestMethod.GET)
    public ReturnInfo getViewToken(@RequestParam Long bimfaceId,
            @RequestParam(defaultValue = "false") Boolean isGeneral,
            @RequestParam(defaultValue = "FILEID") BimFaceService.BimfaceIdType bimfaceIdType,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws IOException,
                                                                                             BimfaceProcessingException {
        log.info("getViewToken(fileId={})", bimfaceId);
        CodeAndMsg code = CodeAndMsg.E05000200;
        String message = CodeAndMsg.E05000200.getMsg();
        String data = "";
        try {
            if (isGeneral) {
                data = bimFaceService.getViewToken(bimfaceIdType, bimfaceId);
            } else {
                data = bimFaceService.getViewTokenByFileId(BimFaceUtil.getBimfaceClient(bimFaceService.getAppKey(),
                        bimFaceService.getAppSecret(), bimFaceService.getApiHost(), bimFaceService.getFileHost()),
                        tenantId, bimfaceId);
            }
        } catch (BimfaceException e) {
            e.printStackTrace();
            String eMessage = e.getMessage();
            JsonNode jsonNode = objectMapper.readTree(eMessage);
            message = jsonNode.get("code").asText();
            code = CodeAndMsg.E05080001;
        }
        if (StringUtils.isBlank(data)) {
            code = CodeAndMsg.E05080001;
            message = CodeAndMsg.E05080001.getMsg();
        }

        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "上传BIM文件到bimface，并启动文件转换", response = BimOutputBean.class)
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ReturnInfo uploadAndTranslate(@RequestParam MultipartFile bimFile,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
        log.info("uploadAndTranslate(bimFile={})", bimFile.getOriginalFilename());
        CodeAndMsg code = CodeAndMsg.E05000200;
        String message = CodeAndMsg.E05000200.getMsg();
        BimOutputBean data = bimFaceService.uploadAndTranslate(bimFile);

        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "上传BIMFace离线数据包到Minio，bim离线预览", response = String.class)
    @RequestMapping(value = "/upload/dataBag", method = RequestMethod.POST)
    public ReturnInfo offlineDataBagUpload(@RequestPart MultipartFile file,
            @RequestPart String fileName,
            @RequestPart String bucket,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
        log.info("uploadAndTranslate(tenantName={}, bucketName={}, bimFile={})", fileName, bucket,
                file.getOriginalFilename());
        CodeAndMsg code = CodeAndMsg.E05000200;
        String message = CodeAndMsg.E05000200.getMsg();
        String data = bimFaceService.dataBagUpload(fileName, bucket, file);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "查询bimface构建属性查询")
    @GetMapping(value = "/objectTypeIds/{objectTypeId}/{instance}")
    public ReturnInfo queryAttributes(@PathVariable String objectTypeId,
            @PathVariable String instance,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws
                                                                                      DataServiceModelRuntimeException,
                                                                                      EntityNotFoundException,
                                                                                      BimfaceException {
        log.info("queryAttributes(objectTypeId={}, instance={}", objectTypeId, instance);
        CodeAndMsg code = CodeAndMsg.E05000200;
        String message = CodeAndMsg.E05000200.getMsg();
        SingleQueryOutput data = bimFaceService.queryAttributes(objectTypeId, instance, tenantId);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "bimface模型集成")
    @PutMapping(value = "/integrate")
    public ReturnInfo fileIntegrate(@RequestBody BimfaceIntegrateInputBean integrateInput,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws
                                                                                      BimfaceResponseErrorException {
        log.info("fileIntegrate(integrateInput={})", integrateInput);
        CodeAndMsg code = CodeAndMsg.E05000200;
        String message = CodeAndMsg.E05000200.getMsg();
        IntegrateBean data = bimFaceService.fileIntegrate(integrateInput);
        ReturnInfo ri = new ReturnInfo(code, message, data);
        return ri;
    }

    @ApiOperation(value = "上传BIM文件到bimface并进行转换")
    @PostMapping(value = "/translate")
    public ReturnInfo fileUploadAndTranslate(@RequestBody BimFileUploadTranslateBean translateInfo,
            @RequestHeader(name = "PCOP-USERID", required = false) String creator,
            @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
        log.info("fileUploadAndTranslate(translateInfo={})", translateInfo);
        bimFaceService.sendMessageUploadAndTranslate(translateInfo);
        ReturnInfo ri = new ReturnInfo(CodeAndMsg.E05000200);
        return ri;
    }
}
