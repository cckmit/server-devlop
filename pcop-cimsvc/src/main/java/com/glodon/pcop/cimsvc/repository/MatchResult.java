package com.glodon.pcop.cimsvc.repository;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author tangd-a
 * @date 2020/4/22 12:36
 */
public class MatchResult {

	private String objectType;
	private Map<String, Object> objectTypeProperties;
	private String containedObjectType;
	private Set<Map<String, Object>> containedObjectPropertiesSet;

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Map<String, Object> getObjectTypeProperties() {
		return objectTypeProperties;
	}

	public void setObjectTypeProperties(Map<String, Object> objectTypeProperties) {
		this.objectTypeProperties = objectTypeProperties;
	}

	public String getContainedObjectType() {
		return containedObjectType;
	}

	public void setContainedObjectType(String containedObjectType) {
		this.containedObjectType = containedObjectType;
	}

	public Set<Map<String, Object>> getContainedObjectPropertiesSet() {
		return containedObjectPropertiesSet;
	}

	public void setContainedObjectPropertiesSet(Set<Map<String, Object>> containedObjectPropertiesSet) {
		this.containedObjectPropertiesSet = containedObjectPropertiesSet;
	}

	@Override
	public String toString() {
		return "MatchResult{" +
				"objectType='" + objectType + '\'' +
				", objectTypeProperties=" + objectTypeProperties +
				", containedObjectType='" + containedObjectType + '\'' +
				", containedObjectPropertiesSet=" + containedObjectPropertiesSet +
				'}';
	}
}
