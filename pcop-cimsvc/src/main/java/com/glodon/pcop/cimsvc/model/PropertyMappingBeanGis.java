package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "属性匹配")
public class PropertyMappingBeanGis {

	@ApiModelProperty(value = "Minio文件ID", required = true)
	private String fileId;

	@ApiModelProperty(value = "对象ID", required = true)
	private String typeId;

	@ApiModelProperty(value = "属性mapping结果", required = true)
	private List<DataSetMappingGis> dataSetMapping;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public List<DataSetMappingGis> getDataSetMapping() {
		return dataSetMapping;
	}

	public void setDataSetMapping(List<DataSetMappingGis> dataSetMapping) {
		this.dataSetMapping = dataSetMapping;
	}

}


