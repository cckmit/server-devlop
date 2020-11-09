package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;

/**
 * @author tangd-a
 * @date 2019/4/18 16:17
 */
@ApiModel(value = "批量下载bean")
public class BatchDownloadBean {
    private String bucket;
    private String fileName;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
