package com.glodon.pcop.cimsvc.model;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 查询条件输入
 */
@ApiModel(value = "查询条件输入")
public class QueryConditionsBean {
    @ApiModelProperty(value = "条件类型")
    private String filterType;
    @ApiModelProperty(value = "逻辑类型")
    private FilteringLogic filterLogical;
    @ApiModelProperty(value = "属性ID")
    private String propertyName;
    @ApiModelProperty(value = "第一个参数")
    private String firstParam;
    @ApiModelProperty(value = "第二个参数")
    private String secondParam;

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public FilteringLogic getFilterLogical() {
        return filterLogical;
    }

    public void setFilterLogical(FilteringLogic filterLogical) {
        this.filterLogical = filterLogical;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(String firstParam) {
        this.firstParam = firstParam;
    }

    public String getSecondParam() {
        return secondParam;
    }

    public void setSecondParam(String secondParam) {
        this.secondParam = secondParam;
    }
}
