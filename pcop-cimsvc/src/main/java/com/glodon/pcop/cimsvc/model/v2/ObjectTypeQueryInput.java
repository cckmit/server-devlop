package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 对象类型查询输入
 */
@ApiModel(description = "对象类型查询输入条件")
public class ObjectTypeQueryInput extends BaseQueryInputBean {
    @ApiModelProperty(value = "对象名称")
    private String objectTypeDesc;

    @ApiModelProperty(value = "对象ID")
    private String objectTypeId;

    private List<String> sortAttributes;
    private ExploreParameters.SortingLogic sortingLogic = ExploreParameters.SortingLogic.ASC;

    @JsonProperty("name")
    public String getObjectTypeDesc() {
        return objectTypeDesc;
    }

    @JsonProperty("name")
    public void setObjectTypeDesc(String objectTypeDesc) {
        this.objectTypeDesc = objectTypeDesc;
    }

    @JsonProperty("id")
    public String getObjectTypeId() {
        return objectTypeId;
    }

    @JsonProperty("id")
    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public List<String> getSortAttributes() {
        return sortAttributes;
    }

    public void setSortAttributes(List<String> sortAttributes) {
        this.sortAttributes = sortAttributes;
    }

    public ExploreParameters.SortingLogic getSortingLogic() {
        return sortingLogic;
    }

    public void setSortingLogic(ExploreParameters.SortingLogic sortingLogic) {
        this.sortingLogic = sortingLogic;
    }
}
