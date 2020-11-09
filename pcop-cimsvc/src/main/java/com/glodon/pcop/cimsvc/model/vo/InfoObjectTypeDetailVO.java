package com.glodon.pcop.cimsvc.model.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tangd-a
 * @date 2020/6/16 19:09
 */
public class InfoObjectTypeDetailVO {

	private List<InfoObjectTypeDetailVO> childInfoObjectTypes;
	private Date createDateTime;
	private Date updateDateTime;
	private String objectTypeName;
	private String objectTypeDesc;
	private String tenantId;

	public InfoObjectTypeDetailVO(){
		childInfoObjectTypes = new ArrayList<>();
	}

	public List<InfoObjectTypeDetailVO> getChildInfoObjectTypes() {
		return childInfoObjectTypes;
	}

	public void setChildInfoObjectTypes(List<InfoObjectTypeDetailVO> childInfoObjectTypes) {
		this.childInfoObjectTypes = childInfoObjectTypes;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getObjectTypeName() {
		return objectTypeName;
	}

	public void setObjectTypeName(String objectTypeName) {
		this.objectTypeName = objectTypeName;
	}

	public String getObjectTypeDesc() {
		return objectTypeDesc;
	}

	public void setObjectTypeDesc(String objectTypeDesc) {
		this.objectTypeDesc = objectTypeDesc;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
