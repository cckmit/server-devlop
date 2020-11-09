package com.glodon.pcop.cimsvc.model.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author tangd-a
 * @date 2019/8/14 18:26
 */
@ApiModel(description = "多源异构融合查询结果")
public class CompositeQueryOutput {
    @ApiModelProperty(value = "总数量", example = "200")
    private Long totalCount;

    @ApiModelProperty(value = "多源异构融合查询结果返回列表")
    private List<CompositeQueryResult> dataInstances;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<CompositeQueryResult> getDataInstances() {
        return dataInstances;
    }

    public void setDataInstances(List<CompositeQueryResult> dataInstances) {
        this.dataInstances = dataInstances;
    }
}
