package com.glodon.pcop.cimsvc.model.v2.gis;

import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "空间分析查询输入条件")
public class SpatialAnalysisQueryInput {
    private List<CommonQueryConditionsBean> generalConditions;
    private GisSqlQueryInput spatialCondition;
    private List<String> objectTypeIds;
    private String cimIdKey;
    private String objectIdKey;

    public List<CommonQueryConditionsBean> getGeneralConditions() {
        return generalConditions;
    }

    public void setGeneralConditions(List<CommonQueryConditionsBean> generalConditions) {
        this.generalConditions = generalConditions;
    }

    public GisSqlQueryInput getSpatialCondition() {
        return spatialCondition;
    }

    public void setSpatialCondition(GisSqlQueryInput spatialCondition) {
        this.spatialCondition = spatialCondition;
    }

    public List<String> getObjectTypeIds() {
        return objectTypeIds;
    }

    public void setObjectTypeIds(List<String> objectTypeIds) {
        this.objectTypeIds = objectTypeIds;
    }

    public String getCimIdKey() {
        return cimIdKey;
    }

    public void setCimIdKey(String cimIdKey) {
        this.cimIdKey = cimIdKey;
    }

    public String getObjectIdKey() {
        return objectIdKey;
    }

    public void setObjectIdKey(String objectIdKey) {
        this.objectIdKey = objectIdKey;
    }

    @Override
    public String toString() {
        return "SpatialAnalysisQueryInput{" +
                "generalConditions=" + generalConditions +
                ", spatialCondition=" + spatialCondition +
                ", objectTypeIds=" + objectTypeIds +
                ", cimIdKey='" + cimIdKey + '\'' +
                ", objectIdKey='" + objectIdKey + '\'' +
                '}';
    }
}
