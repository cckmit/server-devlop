package com.glodon.pcop.cimsvc.model.stat;

import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class StatParamBean {
    @ApiModelProperty(value = "属性名称", required = true)
    private String property;

    @ApiModelProperty(value = "查询标记", required = true)
    private String statItem;

    @ApiModelProperty(value = "统计类型", required = true)
    private String statType;

    @ApiModelProperty(value = "条件", required = false)
    private List<QueryConditionsBean> conditions;
    @ApiModelProperty(value = "如何分组", required = false)
    private List<String> groupAttributes;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getStatItem() {
        return statItem;
    }

    public void setStatItem(String statItem) {
        this.statItem = statItem;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public List<QueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public List<String> getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(List<String> groupAttributes) {
        this.groupAttributes = groupAttributes;
    }
}
