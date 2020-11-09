package com.glodon.pcop.cimclt.client.fallback;

import com.glodon.pcop.cimapi.MinioClientApi;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 11:21
 * @Description:
 */
@Component
public class MinioClientFallback implements MinioClientApi {

    @Override
    public ReturnInfo minioFileUpload(String bucket, String fileName, String isOverwrite, MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public ReturnInfo fileShared(String bucket, String fileName, Integer expiredSeconds) throws MinioClientException {
        return null;
    }
}
