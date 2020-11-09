package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModelProperty;

public class RelatedIntsanceQueryInputBean {
    private String relationTypeName;
    private String relationdDirection;
    @ApiModelProperty(value = "startPage", example = "123")
    private int startPage;
    @ApiModelProperty(value = "endPage", example = "123")
    private int endPage;
    @ApiModelProperty(value = "pageSize", example = "123")
    private int pageSize;

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getRelationdDirection() {
        return relationdDirection;
    }

    public void setRelationdDirection(String relationdDirection) {
        this.relationdDirection = relationdDirection;
    }
}
