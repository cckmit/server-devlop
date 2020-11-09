package com.glodon.pcop.cimsvc.model.stat;

import org.springframework.web.bind.annotation.PathVariable;

public class IntervalWithDimensionBean {
    private String propertyName;
    private int times;
    private String format;

    DimensionTypeBean dimension;
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public DimensionTypeBean getDimension() {
        return dimension;
    }

    public void setDimension(DimensionTypeBean dimension) {
        this.dimension = dimension;
    }
}

