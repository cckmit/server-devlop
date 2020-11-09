package com.glodon.pcop.cimsvc.model.definition;

import io.swagger.annotations.ApiModelProperty;

public class PropertyBatchBean extends BaseBatchBean {
    private String alias;
    private String type;
    private String dictIndex;
    private String defaultValue;
    private boolean isNull;
    private boolean isPrimary;
    @ApiModelProperty(value = "第几页", example = "123")
    private int dataSetIndex;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDictIndex() {
        return dictIndex;
    }

    public void setDictIndex(String dictIndex) {
        this.dictIndex = dictIndex;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean getIsNull() {
        return isNull;
    }

    public void setIsNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getDataSetIndex() {
        return dataSetIndex;
    }

    public void setDataSetIndex(int dataSetIndex) {
        this.dataSetIndex = dataSetIndex;
    }
}
