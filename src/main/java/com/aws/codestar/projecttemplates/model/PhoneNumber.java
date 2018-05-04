package com.aws.codestar.projecttemplates.model;

public class PhoneNumber {
	private long id;
	
	private String number;
	
	private long userId;
	
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public PhoneNumber withId(long id) {
		this.id = id;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PhoneNumber withNumber(String number) {
		this.number = number;
		return this;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long user_id) {
		this.userId = user_id;
	}
	
	public PhoneNumber withUserId(long user_id) {
		this.userId = user_id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public PhoneNumber withName(String name) {
		this.name = name;
		return this;
	}
	
	public String toString() {
		return 	"id: " + this.id + "\n" +
				"number: " + this.number + "\n" +
				"user_id: " + this.userId + "\n" +
				"name: " + this.name + "\n";
	}
}
