package com.glodon.pcop.cimsvc.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tangd-a
 * @date 2019/11/18 15:12
 */
public class OutputQueryFroNameVO {

	private AtomicInteger totalCount;

	
	List<OutputQueryFroNameBean> instanceInfoList;


	public AtomicInteger getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(AtomicInteger totalCount) {
		this.totalCount = totalCount;
	}

	public List<OutputQueryFroNameBean> getInstanceInfoList() {
		return instanceInfoList;
	}

	public void setInstanceInfoList(List<OutputQueryFroNameBean> instanceInfoList) {
		this.instanceInfoList = instanceInfoList;
	}
}

