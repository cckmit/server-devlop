package com.glodon.pcop.cimsvc.model.stat;

import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;

import java.util.List;

public class StatCondtionBean {
    private List<CommonQueryConditionsBean> conditions;
    private DimensionTypeBean dimension;

    public List<CommonQueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<CommonQueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public DimensionTypeBean getDimension() {
        return dimension;
    }

    public void setDimension(DimensionTypeBean dimension) {
        this.dimension = dimension;
    }
}
