package com.glodon.pcop.cimsvc.controller.minio;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.constant.FileImportTypeEnum;
import com.glodon.pcop.cim.common.model.FileUploadStatusBean;
import com.glodon.pcop.cim.common.model.minio.DeleteFileInputBean;
import com.glodon.pcop.cim.common.model.minio.DeleteFileOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import com.glodon.pcop.cimsvc.model.ZipFileStructureBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.core.security.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
@Api(tags = "Minio服务")
@RestController
public class MinioClientController {
    private static Logger log = LoggerFactory.getLogger(MinioClientController.class);

    @Autowired
    private MinioService minioService;

    @ApiOperation(value = "文件上传", notes = "上传本地文件到Minio", response = FileUploadStatusBean.class)
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ReturnInfo fileUpload(@RequestPart String bucket,
                                 @RequestPart String fileName,
                                 @RequestPart(required = false) String isOverwrite,
                                 @RequestPart MultipartFile file,
                                 @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                 @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) throws IOException {
        log.info("imageUpload2Cim(bucket={}, fileName={}, isOverwrite={})", bucket, fileName, isOverwrite);
        FileUploadStatusBean fileUploadStatusBean = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            Boolean isOver = isOverwrite == null;
            fileUploadStatusBean = minioService.uploader(bucket, fileName, inputStream, isOver);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                fileUploadStatusBean);
        return ri;
    }

    @ApiOperation(value = "批量文件下载", notes = "从Minio批量下载文件")
    @ApiImplicitParams({@ApiImplicitParam(name = "fileList", value = "file jsonArray"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "query用户id", dataType = "string"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "query租户id", dataType = "string")})
    @RequestMapping(value = "/batchDownload", method = RequestMethod.GET)
    public void batchFileDownload(@RequestParam String fileList,
                                  @RequestParam(name = "PCOP-USERID") String queryUserId,
                                  @RequestParam(name = "PCOP-TENANTID") String queryTenantId,
                                  HttpServletResponse response) {
        log.info("batchFileDownload(fileList={})", fileList);
        minioService.downloader(fileList, response, queryTenantId, queryUserId);
    }

    @ApiOperation(value = "文件分享链接", notes = "获取文件分享链接")
    @ApiImplicitParams({@ApiImplicitParam(name = "bucket", value = "minio bucket name", required = true, dataType =
            "string", paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, dataType = "string", paramType =
                    "query"),
            @ApiImplicitParam(name = "expiredSeconds", value = "超时时长（秒）,默认7天", dataType = "integer", paramType =
                    "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", dataType = "string", paramType = "header")})
    @RequestMapping(value = "/sharedUrl", method = RequestMethod.GET)
    public ReturnInfo fileShared(@RequestParam String bucket,
                                 @RequestParam String fileName,
                                 @RequestParam(required = false) Integer expiredSeconds,
                                 @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                 @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId,
                                 HttpServletResponse response) throws MinioClientException {
        log.info("sharedUrl(bucket={}, fileName={}, expiredSeconds={})", bucket, fileName, expiredSeconds);
        String sharedUrl = minioService.presignedGetObject(bucket, fileName, expiredSeconds);
        if (!minioService.getShareUrl().equals("")) {
            sharedUrl = sharedUrl.replace(minioService.getUrl(), minioService.getShareUrl());
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                sharedUrl);
        return ri;
    }

    @ApiOperation(value = "office文件预览", notes = "office转pdf预览")
    @RequestMapping(value = "/officePreview", method = RequestMethod.GET)
    public void officePreview(@RequestParam("bucket") String bucket,
                              @RequestParam("fileName") String fileName,
                              @RequestParam(value = "fileContentType", required = false) String fileContentType,
                              @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                              @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId,
                              HttpServletResponse response) {
        log.info("officePreview(bucket={}, fileName={})", bucket, fileName);
        minioService.officePreviewV2(bucket, fileName, response);
    }

    @ApiOperation(value = "文件上传", notes = "文件上传，无租户和用户", hidden = true)
    @RequestMapping(value = "/minioFileUpload", method = RequestMethod.POST, consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReturnInfo minioFileUpload(@RequestParam String bucket,
                                      @RequestParam String fileName,
                                      @RequestParam(required = false) String isOverwrite,
                                      @RequestPart MultipartFile file) throws IOException {
        return fileUpload(bucket, fileName, isOverwrite, file, String.valueOf(SecurityUtils.getCurrentUserId()),
                String.valueOf(SecurityUtils.getCurrentTenantId()));
    }

    @ApiOperation(value = "文件下载", notes = "文件下载，无租户和用户", hidden = true)
    @RequestMapping(value = "/minioDownload", method = RequestMethod.GET)
    public byte[] minioFileDownload(@RequestParam String bucket,
                                    @RequestParam String fileName) {
        log.info("minioDownload(bucket={}, fileName={})", bucket, fileName);
        return minioService.downloader(bucket, fileName);
    }

    @ApiOperation(value = "文件下载", notes = "从Minio下载文件")
    @ApiImplicitParams({@ApiImplicitParam(name = "bucket", value = "minio bucket name", required = true, dataType =
            "string", paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, dataType = "string", paramType =
                    "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "query用户id", dataType = "string"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "query租户id", dataType = "string")})
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void fileDownload(@RequestParam("bucket") String bucket,
                             @RequestParam("fileName") String fileName,
                             @RequestParam(name = "PCOP-USERID") String queryUserId,
                             @RequestParam(name = "PCOP-TENANTID") String queryTenantId,
                             HttpServletResponse response) {
        log.info("fileDownload(bucket={}, fileName={})", bucket, fileName);
        minioService.downloader(bucket, fileName, response, queryTenantId, queryUserId);
    }

    @ApiOperation(value = "zip文件结构", notes = "zip文件列表，指定类型文件的内容", response = ZipFileStructureBean.class,
            responseContainer = "List")
    @RequestMapping(value = "/zipFileStructure", method = RequestMethod.GET)
    public ReturnInfo zipFileStructure(@RequestParam("bucket") String bucket,
                                       @RequestParam("fileName") String fileName,
                                       @RequestParam("fileImportTypeEnum") FileImportTypeEnum fileImportTypeEnum,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("zipFileStructure(bucket={}, fileName={}, fileImportTypeEnum={})", bucket, fileName,
                fileImportTypeEnum);
        List<FileStructBean> zipFileStructureBeans = minioService.zipFileStructure(bucket, fileName,
                fileImportTypeEnum);

        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                zipFileStructureBeans);
    }

    @ApiOperation(value = "minio文件删除", notes = "minio文件删除", response = DeleteFileOutputBean.class)
    @RequestMapping(value = "/minioFile/deleteIfExists", method = RequestMethod.POST)
    public ReturnInfo deleteIfExists(@RequestBody DeleteFileInputBean deleteInput,
                                     @RequestHeader(name = "PCOP-USERID") String userId,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("deleteIfExists(deleteInput={})", JSON.toJSONString(deleteInput));
        return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                minioService.deleteIfExists(deleteInput));
    }

}
