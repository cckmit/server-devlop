package com.glodon.pcop.cimsvc.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author yuanjk 行业分类定义
 */
public class IndustryTypeEntityV1 extends BaseEntity {
    @ApiModelProperty(value = "行业分类Id", required = false)
    private String typeId;

    @ApiModelProperty(value = "行业分类名称", required = true)
    private String typeName;

    @ApiModelProperty(value = "行业分类描述，暂与名称相同", required = false)
    private String description;

    @ApiModelProperty(value = "父分类typeId", required = false)
    private String parentTypeId;

    @ApiModelProperty(value = "创建该分类的用户Id", required = false)
    private String creatorId;

    @ApiModelProperty(value = "分类级别", example = "123", required = true)
    private Integer depth;

    @ApiModelProperty(value = "是否删除：0=删除，1=可用", example = "1", required = false, allowableValues = "0,1")
    private Integer isDelFlag;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(String parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getIsDelFlag() {
        return isDelFlag;
    }

    public void setIsDelFlag(Integer isDelFlag) {
        this.isDelFlag = isDelFlag;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

}
