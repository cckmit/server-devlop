package com.glodon.pcop.cimsvc.model.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @model 字典
 *
 * */
@ApiModel(value = "Dict", description = "属性")
@Data
public class Dict {

    @ApiModelProperty(value = "属性名称")
    private String fieldName;


    @ApiModelProperty(value = "字典名称")
    private String dictName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
