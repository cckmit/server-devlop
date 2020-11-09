package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 查询条件输入
 */
@ApiModel(value = "通用查询条件输入")
public class CommonQueryConditionsBean {
    @ApiModelProperty(value = "条件类型", allowableValues = "EqualFilteringItem, GreaterThanFilteringItem, " +
            "GreaterThanEqualFilteringItem, LessThanFilteringItem, LessThanEqualFilteringItem, NotEqualFilteringItem," +
            " SimilarFilteringItem, InValueFilteringItem, BetweenFilteringItem")
    private String filterType;
    @ApiModelProperty(value = "逻辑类型")
    private FilteringLogic filterLogical;
    @ApiModelProperty(value = "属性ID")
    private String propertyName;
    @ApiModelProperty(value = "第一个参数")
    private String firstParam;
    @ApiModelProperty(value = "第二个参数")
    private String secondParam;
    @ApiModelProperty(value = "List参数")
    private List<String> listParam;

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    @JsonProperty("logic")
    public FilteringLogic getFilterLogical() {
        return filterLogical;
    }

    @JsonProperty("logic")
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

    public List<String> getListParam() {
        return listParam;
    }

    public void setListParam(List<String> listParam) {
        this.listParam = listParam;
    }

    @Override
    public String toString() {
        return "CommonQueryConditionsBean{" +
                "filterType='" + filterType + '\'' +
                ", filterLogical=" + filterLogical +
                ", propertyName='" + propertyName + '\'' +
                ", firstParam='" + firstParam + '\'' +
                ", secondParam='" + secondParam + '\'' +
                ", listParam=" + listParam +
                '}';
    }
}
