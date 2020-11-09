package com.glodon.pcop.cim.common.model.relationship;

import java.util.Map;

public class AddInstanceRelationTargetOutputBean {
    private String objectTypeId;
    private Map<String, Boolean> instanceIds;

    public AddInstanceRelationTargetOutputBean(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public AddInstanceRelationTargetOutputBean() {
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public Map<String, Boolean> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(Map<String, Boolean> instanceIds) {
        this.instanceIds = instanceIds;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationTargetOutputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", instanceIds=" + instanceIds +
                '}';
    }
}
