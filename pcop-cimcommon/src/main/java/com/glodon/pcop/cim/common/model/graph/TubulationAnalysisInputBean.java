package com.glodon.pcop.cim.common.model.graph;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "爆管分析输入")
public class TubulationAnalysisInputBean {
    @NotNull
    private String relationTypeName;
    @NotNull
    private RelationDirection relationDirection;
    @NotNull
    private OutputInstance sourceInstance;
    @NotEmpty
    private List<String> targetObjectTypeIds;

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }

    public OutputInstance getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(OutputInstance sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    public List<String> getTargetObjectTypeIds() {
        return targetObjectTypeIds;
    }

    public void setTargetObjectTypeIds(List<String> targetObjectTypeIds) {
        this.targetObjectTypeIds = targetObjectTypeIds;
    }

    @Override
    public String toString() {
        return "TubulationAnalysisInputBean{" +
                "relationTypeName='" + relationTypeName + '\'' +
                ", relationDirection=" + relationDirection +
                ", sourceInstance=" + sourceInstance +
                ", targetObjectTypeIds=" + targetObjectTypeIds +
                '}';
    }
}

