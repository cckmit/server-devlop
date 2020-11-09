package com.glodon.pcop.cimclt.client.fallback;

import com.glodon.pcop.cimapi.MinioDownloadClientApi;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/15 16:43
 * @Description:
 */
@Component
public class MinioDownloadClientFallback implements MinioDownloadClientApi {
    @Override
    public byte[] fileDownload(String bucket, String fileName) {
        return null;
    }
}
