package com.glodon.pcop.cimsvc.model.output;

import com.glodon.pcop.cimsvc.model.input.InstanceBaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "实例是否连通查询输出")
public class ConnectedOutputBean {
    @ApiModelProperty(value = "起始实例", position = 1)
    private InstanceBaseBean sourceInstance;
    @ApiModelProperty(value = "目标实例列表", position = 2)
    private List<InstanceOutputBean> destinationInstances;

    public InstanceBaseBean getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(InstanceBaseBean sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    public List<InstanceOutputBean> getDestinationInstances() {
        return destinationInstances;
    }

    public void setDestinationInstances(List<InstanceOutputBean> destinationInstances) {
        this.destinationInstances = destinationInstances;
    }

    @Override
    public String toString() {
        return "ConnectedOutputBean{" +
                "sourceInstance=" + sourceInstance +
                ", destinationInstances=" + destinationInstances +
                '}';
    }
}
