package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModelProperty;

public class UIViewInputBean {
    @ApiModelProperty(value = "view唯一标识")
    private String viewId;
    @ApiModelProperty(value = "view关联的对象类型")
    private String infoObjectTypeName;
    @ApiModelProperty(value = "view data")
    private String viewData;
    @ApiModelProperty(value = "view sql")
    private String viewSql;

    public String getInfoObjectTypeName() {
        return infoObjectTypeName;
    }

    public void setInfoObjectTypeName(String infoObjectTypeName) {
        this.infoObjectTypeName = infoObjectTypeName;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getViewData() {
        return viewData;
    }

    public void setViewData(String viewData) {
        this.viewData = viewData;
    }

    public String getViewSql() {
        return viewSql;
    }

    public void setViewSql(String viewSql) {
        this.viewSql = viewSql;
    }
}

