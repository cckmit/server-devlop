package com.glodon.pcop.cimsvc.model.v2.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(description = "文件结构")
public class FileStructBean {
    @ApiModelProperty(name = "单个文件名称")
    private String singleFileName;
    @ApiModelProperty(name = "文件的结构，key：属性名称，value：属性类型")
    private Map<String, String> struct;
    @JsonIgnore
    @ApiModelProperty(name = "totalCount", example = "123")
    private int totalCount;

    @ApiModelProperty(name = "shp features type")
    private String geoType;

    public FileStructBean(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public FileStructBean() {
    }

    public String getSingleFileName() {
        return singleFileName;
    }

    public void setSingleFileName(String singleFileName) {
        this.singleFileName = singleFileName;
    }

    public Map<String, String> getStruct() {
        return struct;
    }

    public void setStruct(Map<String, String> struct) {
        this.struct = struct;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getGeoType() {
        return geoType;
    }

    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }
}
