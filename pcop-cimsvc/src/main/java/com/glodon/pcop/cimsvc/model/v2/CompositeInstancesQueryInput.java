package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "一体化查询输入条件")
public class CompositeInstancesQueryInput {
    @JsonIgnore
    @ApiModelProperty(example = "50")
    private int resultNumber;
    private List<CommonQueryConditionsBean> generalConditions;
    @JsonIgnore
    private String type;
    private List<String> sortAttributes;
    private ExploreParameters.SortingLogic sortingLogic = ExploreParameters.SortingLogic.ASC;

    private GisSpatialQueryConditionInputBean spatialCondition;

    private TemporalQueryConditionInputBean temporalCondition;

    private List<String> objectTypeIds;

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public List<CommonQueryConditionsBean> getGeneralConditions() {
        return generalConditions;
    }

    public void setGeneralConditions(List<CommonQueryConditionsBean> generalConditions) {
        this.generalConditions = generalConditions;
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

    public GisSpatialQueryConditionInputBean getSpatialCondition() {
        return spatialCondition;
    }

    public void setSpatialCondition(GisSpatialQueryConditionInputBean spatialCondition) {
        this.spatialCondition = spatialCondition;
    }

    public TemporalQueryConditionInputBean getTemporalCondition() {
        return temporalCondition;
    }

    public void setTemporalCondition(TemporalQueryConditionInputBean temporalCondition) {
        this.temporalCondition = temporalCondition;
    }

    public List<String> getObjectTypeIds() {
        return objectTypeIds;
    }

    public void setObjectTypeIds(List<String> objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }

    @Override
    public String toString() {
        return "CompositeInstancesQueryInput{" +
                "resultNumber=" + resultNumber +
                ", generalConditions=" + generalConditions +
                ", type='" + type + '\'' +
                ", sortAttributes=" + sortAttributes +
                ", sortingLogic=" + sortingLogic +
                ", spatialCondition=" + spatialCondition +
                ", temporalCondition=" + temporalCondition +
                ", objectTypeIds=" + objectTypeIds +
                '}';
    }
}
