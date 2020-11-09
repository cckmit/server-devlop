package com.glodon.pcop.cim.common.model.stat;

import javax.validation.constraints.Size;
import java.util.List;

public class MultiplePropertiesStatPropertyFilterBean {
    @Size(min = 1)
    private String property;
    private List<String> propertyValues;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<String> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public String toString() {
        return "MultiplePropertiesStatPropertyFilterBean{" +
                "property='" + property + '\'' +
                ", propertyValues=" + propertyValues +
                '}';
    }
}
