package com.glodon.pcop.cimsvc.model.gis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "通用一体化查询输入条件")
public class GeneralCompositeInstancesQueryInput {
    @JsonIgnore
    @ApiModelProperty(example = "50")
    private int resultNumber;
    private List<CommonQueryConditionsBean> generalConditions;
    @JsonIgnore
    private String type;
    private List<String> sortAttributes;
    private ExploreParameters.SortingLogic sortingLogic = ExploreParameters.SortingLogic.ASC;

    private List<GeneralSpatialQueryConditionInputBean> spatialConditions;

    private List<GeneralTemporalQueryConditionInputBean> temporalConditions;

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

    public List<GeneralSpatialQueryConditionInputBean> getSpatialConditions() {
        return spatialConditions;
    }

    public void setSpatialConditions(List<GeneralSpatialQueryConditionInputBean> spatialConditions) {
        this.spatialConditions = spatialConditions;
    }

    public List<GeneralTemporalQueryConditionInputBean> getTemporalConditions() {
        return temporalConditions;
    }

    public void setTemporalConditions(List<GeneralTemporalQueryConditionInputBean> temporalConditions) {
        this.temporalConditions = temporalConditions;
    }

    public List<String> getObjectTypeIds() {
        return objectTypeIds;
    }

    public void setObjectTypeIds(List<String> objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }

    @Override
    public String toString() {
        return "GeneralCompositeInstancesQueryInput{" +
                "resultNumber=" + resultNumber +
                ", generalConditions=" + generalConditions +
                ", type='" + type + '\'' +
                ", sortAttributes=" + sortAttributes +
                ", sortingLogic=" + sortingLogic +
                ", spatialConditions=" + spatialConditions +
                ", temporalConditions=" + temporalConditions +
                ", objectTypeIds=" + objectTypeIds +
                '}';
    }
}
