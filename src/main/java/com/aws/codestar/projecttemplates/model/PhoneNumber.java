package com.aws.codestar.projecttemplates.model;

public class PhoneNumber {
	private Long id;
	
	private String number;
	
	private Long userId;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public PhoneNumber withId(Long id) {
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long user_id) {
		this.userId = user_id;
	}
	
	public PhoneNumber withUserId(Long user_id) {
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
