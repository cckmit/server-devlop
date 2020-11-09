package com.glodon.pcop.cimsvc.model.stat;


import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

@ApiModel(description = "统计参数")

public class StatParameterBean extends StatParamBean {
    @ApiModelProperty(value = "类型名称", required = true)
    private String cim_object_type;

    private String _sign_;

    @ApiModelProperty(value = "是否按照过滤条件统计", required = false)
    private Boolean filter;

    public String getCim_object_type() {
        return cim_object_type;
    }
    public void setCim_object_type(String cim_object_type) {
        this.cim_object_type = cim_object_type;
    }

    public Boolean getFilter() {
        return filter;
    }

    public void setFilter(Boolean filter) {
        this.filter = filter;
    }


    public String get_sign_() {
        return _sign_;
    }

    public void set_sign_(String _sign_) {
        this._sign_ = _sign_;
    }

    //获取统计的属性
    @JsonIgnore
    public String getStatPro() {
        if (getStatType().toLowerCase().equals("sum")) {
            return getProperty() + "Sum";
        }
        return getProperty() + "Count";
    }
}
