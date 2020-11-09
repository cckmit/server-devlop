package com.glodon.pcop.cimapi.model;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实例查询输入
 */
@ApiModel(description = "实例查询输入条件")
public class InstanceQueryInputBean {
    @ApiModelProperty(value = "对象模型ID")
    private String objectTypeId;

    @ApiModelProperty(value = "属性条件")
    private Map<String, String> conditions;

    @ApiModelProperty(value = "每一页的数量", example = "123")
    private int pageSize;

    @ApiModelProperty(value = "开始页", example = "123")
    private int startPage;

    @ApiModelProperty(value = "结束页", example = "123")
    private int endPage;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public Map<String, String> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, String> conditions) {
        this.conditions = conditions;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

}
