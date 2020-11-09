package com.glodon.pcop.cimsvc.model.input;

import com.glodon.pcop.cim.engine.dataServiceEngine.util.path.OrientDBShortestPath;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "实例是否连通查询输入")
public class ConnectedInputBean {
    @ApiModelProperty(value = "起始实例", position = 1)
    private InstanceBaseBean sourceInstance;
    @ApiModelProperty(value = "目标实例列表", position = 2)
    private List<InstanceBaseBean> destinationInstances;
    @ApiModelProperty(value = "关系类型", position = 3)
    private String relationType;
    @ApiModelProperty(value = "连通方向", allowableValues = "BOTH, IN, OUT", position = 4)
    private OrientDBShortestPath.SearchDirectionEnum directionEnum;
    @ApiModelProperty(value = "是否直接连通", position = 5)
    private Boolean directConnect;

    public InstanceBaseBean getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(InstanceBaseBean sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    public List<InstanceBaseBean> getDestinationInstances() {
        return destinationInstances;
    }

    public void setDestinationInstances(List<InstanceBaseBean> destinationInstances) {
        this.destinationInstances = destinationInstances;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public OrientDBShortestPath.SearchDirectionEnum getDirectionEnum() {
        return directionEnum;
    }

    public void setDirectionEnum(OrientDBShortestPath.SearchDirectionEnum directionEnum) {
        this.directionEnum = directionEnum;
    }

    public Boolean getDirectConnect() {
        return directConnect;
    }

    public void setDirectConnect(Boolean directConnect) {
        this.directConnect = directConnect;
    }

    @Override
    public String toString() {
        return "ConnectedInputBean{" +
                "sourceInstance=" + sourceInstance +
                ", destinationInstances=" + destinationInstances +
                ", relationType='" + relationType + '\'' +
                ", directionEnum=" + directionEnum +
                ", isDirectConnect=" + directConnect +
                '}';
    }
}
