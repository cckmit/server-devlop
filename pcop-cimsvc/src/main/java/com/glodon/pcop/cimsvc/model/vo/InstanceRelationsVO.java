package com.glodon.pcop.cimsvc.model.vo;


import com.glodon.pcop.cimsvc.model.graph.instance.InstanceNodeData;
import com.glodon.pcop.cimsvc.model.graph.instance.InstancesEdgeData;

import java.util.List;

public class InstanceRelationsVO {

    private List<InstanceNodeData> instanceNodesList;
    private List<InstancesEdgeData> instancesEdgesList;
    private String focusDataRID;

    public List<InstanceNodeData> getInstanceNodesList() {
        return instanceNodesList;
    }

    public void setInstanceNodesList(List<InstanceNodeData> instanceNodesList) {
        this.instanceNodesList = instanceNodesList;
    }

    public List<InstancesEdgeData> getInstancesEdgesList() {
        return instancesEdgesList;
    }

    public void setInstancesEdgesList(List<InstancesEdgeData> instancesEdgesList) {
        this.instancesEdgesList = instancesEdgesList;
    }

    public String getFocusDataRID() {
        return focusDataRID;
    }

    public void setFocusDataRID(String focusDataRID) {
        this.focusDataRID = focusDataRID;
    }
}
