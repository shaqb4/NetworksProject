package com.aws.codestar.projecttemplates.model;

public class Email {
	
	private long id;
	
	private String email;
	
	private long userId;
	
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Email withId(long id) {
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long user_id) {
		this.userId = user_id;
	}
	
	public Email withUserId(long user_id) {
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
		return 	"id: " + this.id  + "\n" +
				"email: " + this.email+ "\n" +
				"user_id: " + this.userId  + "\n" +
				"name: " + this.name  + "\n";
	}
}
