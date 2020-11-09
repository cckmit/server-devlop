package com.glodon.pcop.cimsvc.model.v2;

public class InstancesByRelationshipQueryInput extends BaseQueryInputBean {
    private String instanceRid;

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }
}
