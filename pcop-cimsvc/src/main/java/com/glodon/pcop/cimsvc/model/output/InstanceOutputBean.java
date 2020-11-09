package com.glodon.pcop.cimsvc.model.output;

import com.glodon.pcop.cimsvc.model.input.InstanceBaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "单实例输出")
public class InstanceOutputBean extends InstanceBaseBean {
    @ApiModelProperty(value = "是否连通")
    private boolean connected;

    public InstanceOutputBean(InstanceBaseBean baseBean) {
        this.setObjectTypeId(baseBean.getObjectTypeId());
        this.setId(baseBean.getId());
    }

    public InstanceOutputBean() {
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return "InstanceOutputBean{" +
                "objectTypeId='" + getObjectTypeId() + '\'' +
                ", id='" + getId() + '\'' +
                ", connected=" + connected +
                '}';
    }
}
