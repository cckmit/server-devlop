package com.glodon.pcop.cimsvc.model.tree;

import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value = "根据查询条件挂载数据输入")
public class LinkObjectAndInstancesByQueryInputBean {
    private NodeInfoBean parentNodeInfo;
    private String objectTypeId;
    private Boolean linkObject;
    private List<CommonQueryConditionsBean> conditions;
    private String sqlWhereCondition;

    public NodeInfoBean getParentNodeInfo() {
        return parentNodeInfo;
    }

    public void setParentNodeInfo(NodeInfoBean parentNodeInfo) {
        this.parentNodeInfo = parentNodeInfo;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public Boolean getLinkObject() {
        return linkObject;
    }

    public void setLinkObject(Boolean linkObject) {
        this.linkObject = linkObject;
    }

    public List<CommonQueryConditionsBean> getConditions() {
        return conditions;
    }

    public void setConditions(List<CommonQueryConditionsBean> conditions) {
        this.conditions = conditions;
    }

    public String getSqlWhereCondition() {
        return sqlWhereCondition;
    }

    public void setSqlWhereCondition(String sqlWhereCondition) {
        this.sqlWhereCondition = sqlWhereCondition;
    }

    @Override
    public String toString() {
        return "LinkObjectAndInstancesByQueryInputBean{" +
                "parentNodeInfo=" + parentNodeInfo +
                ", objectTypeId='" + objectTypeId + '\'' +
                ", linkObject=" + linkObject +
                ", conditions=" + conditions +
                ", sqlWhereCondition='" + sqlWhereCondition + '\'' +
                '}';
    }
}
