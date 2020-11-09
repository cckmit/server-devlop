package com.glodon.pcop.cimapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/15 16:44
 * @Description:
 */
public interface MinioDownloadClientApi {

    @RequestMapping(value = "/minioDownload", method = RequestMethod.GET)
    byte[] fileDownload(@RequestParam String bucket,
                               @RequestParam String fileName);

}
