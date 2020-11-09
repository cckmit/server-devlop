package com.glodon.pcop.cim.common.model.graph;

import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(description = "爆管分析输出")
public class TubulationAnalysisOutputBean {
    private List<OutputInstance> firstLevel;
    private List<OutputInstance> secondLevel;

    public List<OutputInstance> getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(List<OutputInstance> firstLevel) {
        this.firstLevel = firstLevel;
    }

    public List<OutputInstance> getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(List<OutputInstance> secondLevel) {
        this.secondLevel = secondLevel;
    }

    @Override
    public String toString() {
        return "TubulationAnalysisOutputBean{" +
                "firstLevel=" + firstLevel +
                ", secondLevel=" + secondLevel +
                '}';
    }
}

