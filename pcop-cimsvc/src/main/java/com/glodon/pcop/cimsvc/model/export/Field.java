package com.glodon.pcop.cimsvc.model.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @model 属性
 *
 * */
@ApiModel(value = "Field", description = "属性")
@Data
public class Field {

    @ApiModelProperty(value = "属性名称")
    private String fieldName;

    @ApiModelProperty(value = "属性描述")
    private String desc;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
