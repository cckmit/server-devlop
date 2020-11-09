package com.glodon.pcop.cim.common.model.check.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value = "检查，新增对象模型和属性集")
public class CheckAndAddObjectTypeInputBean {
    private String name;
    private String desc;
    @JsonProperty(value = "is_clean")
    private Boolean isClean;
    @JsonProperty(value = "is_create")
    private Boolean isCreate;
    @JsonProperty(value = "is_update")
    private Boolean isUpdate;
    private List<CheckAndAddDataSetInputBean> dataSets;
    @JsonIgnore
    private String tenantId;

    private String commonTag;

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

    @JsonProperty(value = "is_clean")
    public Boolean getClean() {
        return isClean;
    }

    @JsonProperty(value = "is_clean")
    public void setClean(Boolean clean) {
        isClean = clean;
    }

    @JsonProperty(value = "is_create")
    public Boolean getCreate() {
        return isCreate;
    }

    @JsonProperty(value = "is_create")
    public void setCreate(Boolean create) {
        isCreate = create;
    }

    @JsonProperty(value = "is_update")
    public Boolean getUpdate() {
        return isUpdate;
    }

    @JsonProperty(value = "is_update")
    public void setUpdate(Boolean update) {
        isUpdate = update;
    }

    public List<CheckAndAddDataSetInputBean> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<CheckAndAddDataSetInputBean> dataSets) {
        this.dataSets = dataSets;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getCommonTag() {
        return commonTag;
    }

    public void setCommonTag(String commonTag) {
        this.commonTag = commonTag;
    }

    @Override
    public String toString() {
        return "CheckAndAddObjectTypeInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", isClean=" + isClean +
                ", isCreate=" + isCreate +
                ", isUpdate=" + isUpdate +
                ", dataSets=" + dataSets +
                ", tenantId='" + tenantId + '\'' +
                ", commonTag='" + commonTag + '\'' +
                '}';
    }
}
