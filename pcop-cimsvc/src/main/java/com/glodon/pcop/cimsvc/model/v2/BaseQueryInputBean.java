package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ApiModel(value = "分页查询输入基础类")
public class BaseQueryInputBean {
    @ApiModelProperty(value = "第几页，[0, x]", example = "0")
    @Min(value = 0)
    private int pageIndex;
    @ApiModelProperty(value = "每页数量, [0, 10000]", example = "50")
    @Min(value = 0)
    @Max(value = 10000)
    private int pageSize;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @JsonIgnore
    public int getStartPage() {
        return pageIndex + 1;
    }

    @JsonIgnore
    public void setStartPage(int startPage) {
        this.pageIndex = startPage - 1;
    }

    @JsonIgnore
    public int getEndPage() {
        return pageIndex + 2;
    }

    @JsonIgnore
    public void setEndPage(int endPage) {
        this.pageIndex = endPage - 2;
    }
}
