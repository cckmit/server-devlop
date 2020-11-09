package com.glodon.pcop.cimsvc.model.v2;

import java.util.List;

/**
 * @author tangd-a
 * @date 2019/11/18 15:05
 */
public class QueryFroNameInput {


	private List<String> cimIdList;


	public List<String> getCimIdList() {
		return cimIdList;
	}

	public void setCimIdList(List<String> cimIdList) {
		this.cimIdList = cimIdList;
	}
}
