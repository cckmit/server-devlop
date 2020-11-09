package com.glodon.pcop.cim.common.common;

/**
 * CallBackBean定义
 * @author tangd-a
 * @date 2020/7/22 15:39
 */
public class CallBackBean {
	private String appCode;

	private String appkey;

	private String secret;

	private String appName;

	private String contactEmail;

	private String contactPhone;

	private String resourceId;

	private Long timestamp;

	private String userId;

	private String signature;

	public CallBackBean() {
	}

	public CallBackBean(String appCode, String appkey, String secret, String appName, String contactEmail, String contactPhone, String resourceId, Long timestamp, String userId, String signature) {
		this.appCode = appCode;
		this.appkey = appkey;
		this.secret = secret;
		this.appName = appName;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.resourceId = resourceId;
		this.timestamp = timestamp;
		this.userId = userId;
		this.signature = signature;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
