package com.glodon.pcop.cimsvc.model;

/**
 * @author tangd-a
 * @date 2019/11/18 15:04
 */
public class OutputQueryFroNameBean {


	private String objectType;
	private String ID;
	private String NAME;

	public OutputQueryFroNameBean() {
	}


	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}

	@Override
	public String toString() {
		return "OutputQueryFroNameBean{" +
				"objectType='" + objectType + '\'' +
				", ID='" + ID + '\'' +
				", NAME='" + NAME + '\'' +
				'}';
	}
}
