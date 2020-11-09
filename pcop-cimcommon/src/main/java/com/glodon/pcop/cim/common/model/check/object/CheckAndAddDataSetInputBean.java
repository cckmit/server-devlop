package com.glodon.pcop.cim.common.model.check.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean;

import java.util.List;

public class CheckAndAddDataSetInputBean {
    private String name;
    private String desc;
    private List<CheckAndAddPropertyInputBean> linkedProperties;
    @JsonProperty(value = "is_create")
    private Boolean isCreate;

    private String classify;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<CheckAndAddPropertyInputBean> getLinkedProperties() {
        return linkedProperties;
    }

    public void setLinkedProperties(List<CheckAndAddPropertyInputBean> linkedProperties) {
        this.linkedProperties = linkedProperties;
    }

    @JsonProperty(value = "is_create")
    public Boolean getCreate() {
        return isCreate;
    }

    @JsonProperty(value = "is_create")
    public void setCreate(Boolean create) {
        isCreate = create;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    @Override
    public String toString() {
        return "CheckAndAddDataSetInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", linkedProperties=" + linkedProperties +
                ", isCreate=" + isCreate +
                ", classify='" + classify + '\'' +
                '}';
    }
}


