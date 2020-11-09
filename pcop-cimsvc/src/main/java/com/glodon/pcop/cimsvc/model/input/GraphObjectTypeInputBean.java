package com.glodon.pcop.cimsvc.model.input;

/**
 * @author tangd-a
 * @date 2020/6/16 16:42
 */
public class GraphObjectTypeInputBean {

	private String sourceInfoObjectType;
	private String targetInfoObjectType;
	private String relationType;
	private String relationshipDesc;


	public String getSourceInfoObjectType() {
		return sourceInfoObjectType;
	}

	public void setSourceInfoObjectType(String sourceInfoObjectType) {
		this.sourceInfoObjectType = sourceInfoObjectType;
	}

	public String getTargetInfoObjectType() {
		return targetInfoObjectType;
	}

	public void setTargetInfoObjectType(String targetInfoObjectType) {
		this.targetInfoObjectType = targetInfoObjectType;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getRelationshipDesc() {
		return relationshipDesc;
	}

	public void setRelationshipDesc(String relationshipDesc) {
		this.relationshipDesc = relationshipDesc;
	}
}
