package com.glodon.pcop.cim.common.model.relationship;

import java.util.Map;

public class RelatedInstancesOutputBean {
    private String relationRid;
    private String objectTypeId;
    private String objectTypeName;
    private String relationTypeName;
    private Map<String, Object> baseInfo;

    public RelatedInstancesOutputBean(String relationRid, String objectTypeId,String relationTypeName) {
        this.relationRid = relationRid;
        this.objectTypeId = objectTypeId;
        this.relationTypeName = relationTypeName;
    }

    public RelatedInstancesOutputBean() {
    }

    public String getRelationRid() {
        return relationRid;
    }

    public void setRelationRid(String relationRid) {
        this.relationRid = relationRid;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public Map<String, Object> getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(Map<String, Object> baseInfo) {
        this.baseInfo = baseInfo;
    }


}
