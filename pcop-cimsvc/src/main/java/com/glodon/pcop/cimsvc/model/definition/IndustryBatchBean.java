package com.glodon.pcop.cimsvc.model.definition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "分类批量操作")
public class IndustryBatchBean extends BaseBatchBean {
    @ApiModelProperty(value = "层级", example = "123")
    private int level;
    @ApiModelProperty(value = "父索引", example = "123")
    private int parentIndex;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }
}
