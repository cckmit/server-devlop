/**
 *
 */
package com.glodon.pcop.cimsvc.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


/**
 * @author yuanjk 实体基础类
 */
public abstract class BaseEntity {
    @ApiModelProperty(value = "rid", example = "4092599349000")
    private Long rid;
    private Date createTime;
    private Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getRid() {
        return rid;
    }
}
