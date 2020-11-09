package com.glodon.pcop.cimsvc.model;

import java.util.List;

public class JobsDownloadBean {

	private String bucketId;

	private String objectName;

	private String objectTypeId;
	
	private List<PropMappingGis> propertyMapping;

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectTypeId() {
		return objectTypeId;
	}

	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	public List<PropMappingGis> getPropertyMapping() {
		return propertyMapping;
	}

	public void setPropertyMapping(List<PropMappingGis> propertyMapping) {
		this.propertyMapping = propertyMapping;
	}
	
}
