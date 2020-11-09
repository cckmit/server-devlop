package com.glodon.pcop.cim.common.model.graph;

public class OutputInstance {
    private String objectTypeId;
    private String instanceId;
    private String rid;

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

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "TubulationAnalysisOutputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
