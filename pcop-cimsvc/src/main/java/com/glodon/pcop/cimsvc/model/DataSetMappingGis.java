package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DataSetMappingGis {
	@ApiModelProperty(value = "属性集ID", required = true)
	private  String dataSetId;

	@ApiModelProperty(value = "属性集名称", required = true)
	private  String dataSetName;

	@ApiModelProperty(value = "属性mapping结果", required = true)
	private List<PropMappingGis> propertyMapping;

	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public List<PropMappingGis> getPropertyMapping() {
		return propertyMapping;
	}

	public void setPropertyMapping(List<PropMappingGis> propertyMapping) {
		this.propertyMapping = propertyMapping;
	}
}
