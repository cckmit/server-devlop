package com.glodon.pcop.cimsvc.service.bim;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "bimface上传文件输出")
public class BimOutputBean {
    @ApiModelProperty(value = "文件ID", example = "4092599349000")
    private Long fileId;
    private String name;
    private String databagId;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabagId() {
        return databagId;
    }

    public void setDatabagId(String databagId) {
        this.databagId = databagId;
    }
}
