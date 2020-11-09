package com.glodon.pcop.cimsvc.model.worker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "工种人数")
public class WorkerPerTypeBean {

    public WorkerPerTypeBean(String workTypeId, String workTypeName, String count) {
        this.workTypeId = workTypeId;
        this.workTypeName = workTypeName;
        this.count = count;
    }

    @ApiModelProperty(value = "工种ID", required = true)
    private String workTypeId;

    @ApiModelProperty(value = "工种名称", required = true)
    private String workTypeName;

    @ApiModelProperty(value = "工种人数", required = true)
    private String count;

    public String getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(String workTypeId) {
        this.workTypeId = workTypeId;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
