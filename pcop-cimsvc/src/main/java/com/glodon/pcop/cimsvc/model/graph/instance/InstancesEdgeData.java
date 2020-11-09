package com.glodon.pcop.cimsvc.model.graph.instance;

/**
 * @author tangd-a
 * @date 2020/6/24 10:09
 */
public class InstancesEdgeData {

	private String relationTypeName;
	private String relationTypeDesc;
	private String relationRID;
	private String sourceDataRID;
	private String targetDataRID;

	public InstancesEdgeData() {
	}


	public InstancesEdgeData(String relationTypeName, String relationTypeDesc, String relationRID, String sourceDataRID, String targetDataRID) {
		this.relationTypeName = relationTypeName;
		this.relationTypeDesc = relationTypeDesc;
		this.relationRID = relationRID;
		this.sourceDataRID = sourceDataRID;
		this.targetDataRID = targetDataRID;
	}

	public String getRelationTypeName() {
		return relationTypeName;
	}

	public void setRelationTypeName(String relationTypeName) {
		this.relationTypeName = relationTypeName;
	}

	public String getRelationTypeDesc() {
		return relationTypeDesc;
	}

	public void setRelationTypeDesc(String relationTypeDesc) {
		this.relationTypeDesc = relationTypeDesc;
	}

	public String getRelationRID() {
		return relationRID;
	}

	public void setRelationRID(String relationRID) {
		this.relationRID = relationRID;
	}

	public String getSourceDataRID() {
		return sourceDataRID;
	}

	public void setSourceDataRID(String sourceDataRID) {
		this.sourceDataRID = sourceDataRID;
	}

	public String getTargetDataRID() {
		return targetDataRID;
	}

	public void setTargetDataRID(String targetDataRID) {
		this.targetDataRID = targetDataRID;
	}
}
