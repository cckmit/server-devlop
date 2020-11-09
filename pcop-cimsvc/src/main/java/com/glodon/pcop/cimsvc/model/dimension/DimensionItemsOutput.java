package com.glodon.pcop.cimsvc.model.dimension;

import java.util.List;
import java.util.Map;

public class DimensionItemsOutput {
    private String dimensionTypeName;
    private List<Map<String, Object>> items;

    public String getDimensionTypeName() {
        return dimensionTypeName;
    }

    public void setDimensionTypeName(String dimensionTypeName) {
        this.dimensionTypeName = dimensionTypeName;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
