package com.aws.codestar.projecttemplates.model;

public class Email {
	
	private Long id;
	
	private String email;
	
	private Long userId;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Email withId(Long id) {
		this.id = id;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Email withEmail(String email) {
		this.email = email;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long user_id) {
		this.userId = user_id;
	}
	
	public Email withUserId(Long user_id) {
		this.userId = user_id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Email withName(String name) {
		this.name = name;
		return this;
	}
	
	public String toString() {
		return 	"id: " + this.id + "\n" +
				"email: " + this.email + "\n" +
				"user_id: " + this.userId + "\n" +
				"name: " + this.name + "\n";
	}
}
