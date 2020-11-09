package com.glodon.pcop.cimstatsvc.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

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
    private String firstParm;
    @ApiModelProperty(value = "第二个参数")
    private String secondParm;
    @ApiModelProperty(value = "参数列表")
    private List<String> inParms;

    public List<String> getListParm() {
        return listParm;
    }

    public void setListParm(List<String> listParm) {
        this.listParm = listParm;
    }

    private List<String> listParm;
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

    public String getFirstParm() {
        return firstParm;
    }

    public void setFirstParm(String firstParm) {
        this.firstParm = firstParm;
    }

    public String getSecondParm() {
        return secondParm;
    }

    public void setSecondParm(String secondParm) {
        this.secondParm = secondParm;
    }

    public List<String> getInParms() {
        return inParms;
    }

    public void setInParms(List<String> inParms) {
        this.inParms = inParms;
    }
}
