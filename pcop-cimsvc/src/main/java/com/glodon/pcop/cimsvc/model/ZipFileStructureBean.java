package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel(value = "压缩文件内部结构")
public class ZipFileStructureBean {
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @ApiModelProperty(value = "文件结构，字段名称：字段类型")
    private List<Map<String, String>> fileStructure;

    public ZipFileStructureBean(String fileName) {
        this.fileName = fileName;
    }

    public ZipFileStructureBean() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, String>> getFileStructure() {
        return fileStructure;
    }

    public void setFileStructure(List<Map<String, String>> fileStructure) {
        this.fileStructure = fileStructure;
    }
}
