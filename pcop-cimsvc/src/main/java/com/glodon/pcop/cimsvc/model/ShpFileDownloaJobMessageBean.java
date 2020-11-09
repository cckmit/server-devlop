package com.glodon.pcop.cimsvc.model;

public class ShpFileDownloaJobMessageBean {

	private String bucketId;

	private PropertyMappingBeanGis propertyMapping;

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public PropertyMappingBeanGis getPropertyMapping() {
		return propertyMapping;
	}

	public void setPropertyMapping(PropertyMappingBeanGis propertyMapping) {
		this.propertyMapping = propertyMapping;
	}
}
