package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ObjectTypeRelatedInstancesQueryInput {
    @ApiModelProperty(value = "第几页，[0, x]", example = "0")
    @Min(value = 0)
    private int pageIndex;
    @ApiModelProperty(value = "每页数量, [0, 10000]", example = "50")
    @Min(value = 0)
    @Max(value = 10000)
    private int pageSize;
    private String id;
    private String instanceRid;
    private RelationDirection relationDirection;
    private String relatedObjectTypeId;

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

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }

    public String getRelatedObjectTypeId() {
        return relatedObjectTypeId;
    }

    public void setRelatedObjectTypeId(String relatedObjectTypeId) {
        this.relatedObjectTypeId = relatedObjectTypeId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
