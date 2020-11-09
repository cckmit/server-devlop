package com.glodon.pcop.cimsvc.model;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "SHP文件结构")
public class FileStructureBean {

    public FileStructureBean() {
        super();
    }

    public FileStructureBean(String fileId, Map<String, String> structure) {
        super();
        this.fileId = fileId;
        this.structure = structure;
    }

    public FileStructureBean(String fileId, long totalCount, Map<String, String> structure) {
        super();
        this.fileId = fileId;
        this.totalCount = totalCount;
        this.structure = structure;
    }

    @ApiModelProperty(value = "上传Minio后得到的文件ID", required = true)
    private String fileId;

    @ApiModelProperty(value = "文件记录条数", example = "4092599349000", required = true)
    private long totalCount;

    @ApiModelProperty(value = "文件结构", required = true)
    private Map<String, String> structure;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, String> getStructure() {
        return structure;
    }

    public void setStructure(Map<String, String> structure) {
        this.structure = structure;
    }


}
