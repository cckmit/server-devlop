package com.glodon.pcop.cimsvc.model.graph.instance;

/**
 * @author tangd-a
 * @date 2020/6/24 10:07
 */
public class InstanceNodeData {

	private String dataTypeName;
	private String dataTypeDesc;
	private String dataRID;
	private String dataClassify;

	public InstanceNodeData(String dataTypeName, String dataTypeDesc, String dataRID, String dataClassify) {
		this.dataTypeName = dataTypeName;
		this.dataTypeDesc = dataTypeDesc;
		this.dataRID = dataRID;
		this.dataClassify = dataClassify;
	}

	public InstanceNodeData() {
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public String getDataTypeDesc() {
		return dataTypeDesc;
	}

	public void setDataTypeDesc(String dataTypeDesc) {
		this.dataTypeDesc = dataTypeDesc;
	}

	public String getDataRID() {
		return dataRID;
	}

	public void setDataRID(String dataRID) {
		this.dataRID = dataRID;
	}

	public String getDataClassify() {
		return dataClassify;
	}

	public void setDataClassify(String dataClassify) {
		this.dataClassify = dataClassify;
	}
}

