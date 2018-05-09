package com.aws.codestar.projecttemplates.model;

public class Client {

	private Long id;
	
	private String name;
	
	private String token;

	private Long userId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Client withId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Client withName(String name) {
		this.name = name;
		return this;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public Client withToken(String token) {
		this.token = token;
		return this;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long user_id) {
		this.userId = user_id;
	}
	
	public Client withUserId(Long user_id) {
		this.userId = user_id;
		return this;
	}
}
