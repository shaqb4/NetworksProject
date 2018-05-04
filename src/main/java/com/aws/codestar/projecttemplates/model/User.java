package com.aws.codestar.projecttemplates.model;

public class User {
	private Long id;
	
	private String first;
	
	private String last;
	
	private String username;
	
	private String password;
	
	private String salt;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public User withId(Long id) {
		this.id = id;
		return this;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}
	
	public User withFirst(String first) {
		this.first = first;
		return this;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public User withLast(String last) {
		this.last = last;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public User withUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public User withPassword(String password) {
		this.password = password;
		return this;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public User withSalt(String salt) {
		this.salt = salt;
		return this;
	}
}
