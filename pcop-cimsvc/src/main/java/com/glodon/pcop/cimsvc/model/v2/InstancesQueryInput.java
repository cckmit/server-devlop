package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "单实例数据查询输入参数")
public class InstancesQueryInput extends BaseQueryInputBean {
    @JsonIgnore
    @ApiModelProperty(example = "50")
    private int resultNumber;
    private List<CommonQueryConditionsBean> conditions;
    @JsonIgnore
    private String type;
    private List<String> sortAttributes;
    private ExploreParameters.SortingLogic sortingLogic = ExploreParameters.SortingLogic.ASC;

    private String sqlWhereCondition;

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public List<CommonQueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<CommonQueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("order_by")
    public List<String> getSortAttributes() {
        return sortAttributes;
    }

    @JsonProperty("order_by")
    public void setSortAttributes(List<String> sortAttributes) {
        this.sortAttributes = sortAttributes;
    }

    @JsonProperty("sort")
    public ExploreParameters.SortingLogic getSortingLogic() {
        return sortingLogic;
    }

    @JsonProperty("sort")
    public void setSortingLogic(ExploreParameters.SortingLogic sortingLogic) {
        this.sortingLogic = sortingLogic;
    }

    public String getSqlWhereCondition() {
        return sqlWhereCondition;
    }

    public void setSqlWhereCondition(String sqlWhereCondition) {
        this.sqlWhereCondition = sqlWhereCondition;
    }

    @Override
    public String toString() {
        return "InstancesQueryInput{" +
                "resultNumber=" + resultNumber +
                ", conditions=" + conditions +
                ", type='" + type + '\'' +
                ", sortAttributes=" + sortAttributes +
                ", sortingLogic=" + sortingLogic +
                ", sqlWhereCondition='" + sqlWhereCondition + '\'' +
                '}';
    }
}
