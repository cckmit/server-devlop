package com.glodon.pcop.cim.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2019/8/29 18:20
 */
public class BimfaceVO {
    @JsonProperty(value = "group")
    private String group;
    @JsonProperty(value = "items")
    private List<Map<String,Object>> items;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public BimfaceVO(String group, List<Map<String, Object>> items) {
        this.group = group;
        this.items = items;
    }
    public BimfaceVO(){

    }

}
