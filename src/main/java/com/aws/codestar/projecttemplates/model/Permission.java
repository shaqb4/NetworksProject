package com.aws.codestar.projecttemplates.model;

public class Permission {
	private Long id;
	
	private Long userId;
	
	private Long client;
	
	private Long profile;
	
	private Long email;
	
	private Long phoneNumber;
	
	private Long address;
	
	private String accessToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Permission withId(Long id) {
		this.id = id;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Permission withUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Long getClient() {
		return client;
	}

	public void setClient(Long client) {
		this.client = client;
	}
	
	public Permission withClient(Long client) {
		this.client = client;
		return this;
	}

	public Long getProfile() {
		return profile;
	}

	public void setProfile(Long profile) {
		this.profile = profile;
	}
	
	public Permission withProfile(Long profile) {
		this.profile = profile;
		return this;
	}

	public Long getEmail() {
		return email;
	}

	public void setEmail(Long email) {
		this.email = email;
	}

	public Permission withEmail(Long email) {
		this.email = email;
		return this;
	}
	
	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Permission withPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public Long getAddress() {
		return address;
	}

	public void setAddress(Long address) {
		this.address = address;
	}

	public Permission withAddress(Long address) {
		this.address = address;
		return this;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public Permission withAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
}
