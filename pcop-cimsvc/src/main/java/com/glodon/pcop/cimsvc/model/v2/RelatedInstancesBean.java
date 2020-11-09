package com.glodon.pcop.cimsvc.model.v2;

import java.util.Map;

public class RelatedInstancesBean {

    public RelatedInstancesBean(String objectTypeName, String instanceId, Map<String, Map<String, Object>> dataSetValue) {
        this.objectTypeId = objectTypeName;
        this.instanceData = dataSetValue;
        this.instanceRid = instanceId;
    }

    public RelatedInstancesBean() {
    }

    private String objectTypeId;
    private String instanceRid;
    private Map<String, Map<String, Object>> instanceData;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public Map<String, Map<String, Object>> getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(Map<String, Map<String, Object>> instanceData) {
        this.instanceData = instanceData;
    }
}
