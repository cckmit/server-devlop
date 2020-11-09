package com.glodon.pcop.cimsvc.model;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import io.swagger.annotations.ApiModelProperty;

public class RelationshipQueryInputBean {
    private String srcObjectTypeId;
    private RelationDirection relationDirection;
    @ApiModelProperty(value = "startPage", example = "123")
    private int startPage;
    @ApiModelProperty(value = "endPage", example = "123")
    private int endPage;
    @ApiModelProperty(value = "pageSize", example = "123")
    private int pageSize;

    public String getSrcObjectTypeId() {
        return srcObjectTypeId;
    }

    public void setSrcObjectTypeId(String srcObjectTypeId) {
        this.srcObjectTypeId = srcObjectTypeId;
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

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }
}
