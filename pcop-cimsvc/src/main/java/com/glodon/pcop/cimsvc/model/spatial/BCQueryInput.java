package com.glodon.pcop.cimsvc.model.spatial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel(value = "缓冲区包含查询输入")
public class BCQueryInput {
    @JsonIgnore
    @ApiModelProperty(value = "选定区域的WKT表示")
    private String wktArea;

    @ApiModelProperty(value = "基本属性查询输入")
    private List<CommonQueryConditionsBean> conditions;

    @ApiModelProperty(value = "缓冲区输入")
    private List<BufferInput> bufferInputs;

    public String getWktArea() {
        return wktArea;
    }

    public void setWktArea(String wktArea) {
        this.wktArea = wktArea;
    }

    public List<BufferInput> getBufferInputs() {
        return bufferInputs;
    }

    public void setBufferInputs(List<BufferInput> bufferInputs) {
        this.bufferInputs = bufferInputs;
    }

    public List<CommonQueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<CommonQueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    @ApiModel(value = "缓冲区输入")
    public static class BufferInput {
        private String objectTypeId;
        @ApiModelProperty(value = "距离（米）", example = "123")
        private int distance;

        public String getObjectTypeId() {
            return objectTypeId;
        }

        public void setObjectTypeId(String objectTypeId) {
            this.objectTypeId = objectTypeId;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }
    }


}
