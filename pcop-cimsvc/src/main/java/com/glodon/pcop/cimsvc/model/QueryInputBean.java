package com.glodon.pcop.cimsvc.model;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class QueryInputBean {
    @ApiModelProperty(value = "pageSize", example = "123")
    private int pageSize;
    @ApiModelProperty(value = "startPage", example = "123")
    private int startPage;
    @ApiModelProperty(value = "endPage", example = "123")
    private int endPage;
    @ApiModelProperty(value = "resultNumber", example = "123")
    private int resultNumber;
    private List<QueryConditionsBean> conditions;
    private String type;
    private List<String> sortAttributes;
    private ExploreParameters.SortingLogic sortingLogic = ExploreParameters.SortingLogic.ASC;

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

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public List<QueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSortAttributes() {
        return sortAttributes;
    }

    public void setSortAttributes(List<String> sortAttributes) {
        this.sortAttributes = sortAttributes;
    }

    public ExploreParameters.SortingLogic getSortingLogic() {
        return sortingLogic;
    }

    public void setSortingLogic(ExploreParameters.SortingLogic sortingLogic) {
        this.sortingLogic = sortingLogic;
    }
}
