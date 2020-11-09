package com.glodon.pcop.cimstatsvc.model;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;

import java.util.List;

public class QueryInputBean {
    private int pageSize;
    private int startPage;
    private int endPage;
    private int resultNumber;
    private List<QueryConditionsBean> conditions;
    private String type;
    private List<String> sortAttributes;
    private List<String> groupAttributes;
    private List<String> poropertys;
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
