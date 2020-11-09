package com.glodon.pcop.cimsvc.model.export;

import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @model 请求体
 *
 *
 * */
@ApiModel(value = "RequestBody", description = "请求体")
@Data
public class ExportModel {

    @ApiModelProperty(value = "项目Id")
    private String projectId;


    @ApiModelProperty(value = "关联对象")
    private List<RelevanceObject> objectTypes;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<RelevanceObject> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<RelevanceObject> objectTypes) {
        this.objectTypes = objectTypes;
    }


  }
