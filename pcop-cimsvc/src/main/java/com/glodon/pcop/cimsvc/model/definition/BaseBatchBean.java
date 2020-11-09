package com.glodon.pcop.cimsvc.model.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "批量操作基础")
public class BaseBatchBean {
    @JsonIgnore
    private String rid;
    @ApiModelProperty(value = "第几页", example = "123")
    private int index;
    private String name;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
