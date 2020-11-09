package com.glodon.pcop.cimsvc.model.export;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @model 关联对象
 *
 * */
@ApiModel(value = "RelevanceObject", description = "关联对象")
@Data
public class RelevanceObject {

    @ApiModelProperty(value = "关联对象Id")
    private String objectTypeId;

    @ApiModelProperty(value = "关联对象名称")
    private String name;

    @ApiModelProperty(value = "属性")
    private List<Field> fields;


    @ApiModelProperty(value = "字典")
    private List<Dict> dicts;

    @ApiModelProperty(value = "日期格式")
    private List<DateFormat> dateFormatList;


    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Dict> getDicts() {
        return dicts;
    }

    public void setDicts(List<Dict> dicts) {
        this.dicts = dicts;
    }

    public List<DateFormat> getDateFormatList() {
        return dateFormatList;
    }

    public void setDateFormatList(List<DateFormat> dateFormatList) {
        this.dateFormatList = dateFormatList;
    }
}
