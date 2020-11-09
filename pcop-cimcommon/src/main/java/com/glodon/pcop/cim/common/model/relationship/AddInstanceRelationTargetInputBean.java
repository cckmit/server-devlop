package com.glodon.pcop.cim.common.model.relationship;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AddInstanceRelationTargetInputBean {
    @NotNull
    private String objectTypeId;
    @Size(min = 1)
    private List<String> instanceIds;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationTargetInputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", instanceIds=" + instanceIds +
                '}';
    }
}
