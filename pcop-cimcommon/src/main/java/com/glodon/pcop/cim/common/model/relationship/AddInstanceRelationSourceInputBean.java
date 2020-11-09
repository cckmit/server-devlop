package com.glodon.pcop.cim.common.model.relationship;

import javax.validation.constraints.NotNull;

public class AddInstanceRelationSourceInputBean {
    @NotNull
    private String objectTypeId;
    @NotNull
    private String instanceId;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationSourceInputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
