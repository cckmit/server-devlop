package com.glodon.pcop.cimapi;

import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 11:14
 * @Description:
 */
public interface MinioClientApi {

    @RequestMapping(value = "/minioFileUpload", method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ReturnInfo minioFileUpload(@RequestParam String bucket,
                                 @RequestParam String fileName,
                                 @RequestParam(required = false) String isOverwrite,
                                 @RequestPart MultipartFile file) throws IOException;

    @RequestMapping(value = "/sharedUrl", method = RequestMethod.GET)
    ReturnInfo fileShared(@RequestParam String bucket,
                                 @RequestParam String fileName,
                                 @RequestParam(required = false) Integer expiredSeconds) throws MinioClientException;
}
