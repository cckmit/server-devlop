package com.glodon.pcop.cimstatsvc.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

@ApiModel(description = "统计参数")

public class StatParameter implements Cloneable{
    @ApiModelProperty(value = "类型名称", required = true)
    private String cim_object_type;
    @ApiModelProperty(value = "属性名称", required = true)
    private String property;

    @ApiModelProperty(value = "查询标记", required = true)
    private String stat_item;

    private String _sign_;

    @ApiModelProperty(value = "统计类型", required = true)
    private String statType;
    @ApiModelProperty(value = "是否按照过滤条件统计", required = false)
    private Boolean filter;
    @ApiModelProperty(value = "条件", required = false)
    private List<QueryConditionsBean> conditions;
    @ApiModelProperty(value = "如何分组", required = false)
    private List<String> groupAttributes;



    public String getCim_object_type() {
        return cim_object_type;
    }

    public List<String> getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(List<String> groupAttributes) {
        this.groupAttributes = groupAttributes;
    }

    public void setCim_object_type(String cim_object_type) {
        this.cim_object_type = cim_object_type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public Boolean getFilter() {
        return filter;
    }

    public void setFilter(Boolean filter) {
        this.filter = filter;
    }


    public List<QueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<QueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public String get_sign_() {
        return _sign_;
    }

    public void set_sign_(String _sign_) {
        this._sign_ = _sign_;
    }


    public String getStat_item() {
        return stat_item;
    }

    public void setStat_item(String stat_item) {
        this.stat_item = stat_item;
    }

    //获取统计的属性
    @JsonIgnore
    public String  getStatPro(){
        if(getStatType().toLowerCase().equals("sum")){
            return getProperty()+ "Sum";
        }
        return getProperty()+ "Count";
    }
}
