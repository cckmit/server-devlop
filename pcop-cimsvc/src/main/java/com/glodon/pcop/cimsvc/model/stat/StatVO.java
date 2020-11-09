package com.glodon.pcop.cimsvc.model.stat;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatVO {
    private String name;
    private String showName;
    private Boolean status;
    private Long totalCount = 0l;
    private List<Map<String, Object>> result = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }


    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }



}
