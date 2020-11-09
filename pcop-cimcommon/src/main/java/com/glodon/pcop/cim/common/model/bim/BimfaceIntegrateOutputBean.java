package com.glodon.pcop.cim.common.model.bim;

import com.bimface.sdk.bean.response.IntegrateBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

@ApiModel(value = "bimface 模型集成输出")
public class BimfaceIntegrateOutputBean {
    private Long integrateId;                            // 合并Id
    private String name;                                   // 合并文件的名字
    private Integer priority;                               // 优先级
    private String status;                                 // 转换状态
    private String[] thumbnail;                              // 缩略图路径
    private String reason;                                 // 失败原因
    private String createTime;                             // 创建时间，格式：yyyy-MM-dd hh:mm:ss

    public BimfaceIntegrateOutputBean(IntegrateBean integrateBean) {
        this.integrateId = integrateBean.getIntegrateId();
        this.name = integrateBean.getName();
        this.priority = integrateBean.getPriority();
        this.status = integrateBean.getStatus();
        this.thumbnail = integrateBean.getThumbnail();
        this.reason = integrateBean.getReason();
        this.createTime = integrateBean.getCreateTime();
    }

    public BimfaceIntegrateOutputBean() {
    }

    public Long getIntegrateId() {
        return integrateId;
    }

    public void setIntegrateId(Long integrateId) {
        this.integrateId = integrateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BimfaceIntegrateOutputBean{" +
                "integrateId=" + integrateId +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", thumbnail=" + Arrays.toString(thumbnail) +
                ", reason='" + reason + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
