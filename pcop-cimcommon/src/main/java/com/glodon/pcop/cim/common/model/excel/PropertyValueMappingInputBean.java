package com.glodon.pcop.cim.common.model.excel;

import java.util.Map;

public class PropertyValueMappingInputBean extends PropertyInputBean {
    private Map<String, String> valueMapping;

    public Map<String, String> getValueMapping() {
        return valueMapping;
    }

    public void setValueMapping(Map<String, String> valueMapping) {
        this.valueMapping = valueMapping;
    }

    @Override
    public String toString() {
        return "PropertyValueMappingInputBean{" +
                "valueMapping=" + valueMapping +
                "} " + super.toString();
    }
}
