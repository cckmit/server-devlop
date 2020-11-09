package com.glodon.pcop.cimsvc.model.v2;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.UIViewVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "Dimension query outout bean")
public class DimensionQueryOutput {
    @ApiModelProperty(value = "总量", example = "4092599349000")
    private Long totalCount;
    @ApiModelProperty(value = "dimension values")
    private List<UIViewVo> dimensions;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<UIViewVo> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<UIViewVo> dimensions) {
        this.dimensions = dimensions;
    }
}
