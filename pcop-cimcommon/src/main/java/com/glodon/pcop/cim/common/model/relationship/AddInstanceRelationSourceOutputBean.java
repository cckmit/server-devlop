package com.glodon.pcop.cim.common.model.relationship;

public class AddInstanceRelationSourceOutputBean {
    private String objectTypeId;
    private String instanceId;
    private Boolean success;

    public AddInstanceRelationSourceOutputBean(AddInstanceRelationSourceInputBean inputBean) {
        this.objectTypeId = inputBean.getObjectTypeId();
        this.instanceId = inputBean.getInstanceId();
        this.success = true;
    }

    public AddInstanceRelationSourceOutputBean() {
    }

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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationSourceOutputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", success=" + success +
                '}';
    }
}
