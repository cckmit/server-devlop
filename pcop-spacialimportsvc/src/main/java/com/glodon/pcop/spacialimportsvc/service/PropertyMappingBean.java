package com.glodon.pcop.spacialimportsvc.service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "属性匹配")
public class PropertyMappingBean {

	@ApiModelProperty(value = "Minio文件ID", required = true)
	private String fileId;

	@ApiModelProperty(value = "对象ID", required = true)
	private String typeId;

	@ApiModelProperty(value = "文件类型")
	private String fileType;

	@ApiModelProperty(value = "属性mapping结果", required = true)
	private List<DataSetMapping> dataSetMapping;

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

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public List<DataSetMapping> getDataSetMapping() {
		return dataSetMapping;
	}

	public void setDataSetMapping(List<DataSetMapping> dataSetMapping) {
		this.dataSetMapping = dataSetMapping;
	}

}

