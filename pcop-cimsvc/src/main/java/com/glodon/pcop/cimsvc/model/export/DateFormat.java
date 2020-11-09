package com.glodon.pcop.cimsvc.model.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @model 日期格式化对象
 *
 * */
@ApiModel(value = "DateFormat", description = "日期格式化对象")
@Data
public class DateFormat {

    @ApiModelProperty(value = "属性名称")
    private String fieldName;


    @ApiModelProperty(value = "格式化样式")
    private String format;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
