package com.glodon.pcop.cim.common.model.stat;

import io.swagger.annotations.ApiModel;

import java.util.List;
import java.util.Map;

@ApiModel(value = "多属性统计输出")
public class MultiplePropertiesStatOutputBean {
    private Map<String, Object> summaryCount;
    private List<Map<String, Object>> detailCount;

    public Map<String, Object> getSummaryCount() {
        return summaryCount;
    }

    public void setSummaryCount(Map<String, Object> summaryCount) {
        this.summaryCount = summaryCount;
    }

    public List<Map<String, Object>> getDetailCount() {
        return detailCount;
    }

    public void setDetailCount(List<Map<String, Object>> detailCount) {
        this.detailCount = detailCount;
    }
}
