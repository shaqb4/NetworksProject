package com.aws.codestar.projecttemplates.model;

public class Profile {

	private Long id;
	
	private Long email;
	
	private Long phoneNumber;
	
	private Long address;
	
	private Long userId;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Profile withId(Long id) {
		this.id = id;
		return this;
	}

	public Long getEmail() {
		return email;
	}

	public void setEmail(Long email) {
		this.email = email;
	}

	public Profile withEmail(Long email) {
		this.email = email;
		return this;
	}
	
	public Long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Profile withPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}
	
	public Long getAddress() {
		return address;
	}

	public void setAddress(Long address) {
		this.address = address;
	}

	public Profile withAddress(Long address) {
		this.address = address;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long user_id) {
		this.userId = user_id;
	}
	
	public Profile withUserId(Long user_id) {
		this.userId = user_id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Profile withName(String name) {
		this.name = name;
		return this;
	}
	
	public String toString() {
		return 	"id: " + this.id + "\n" +
				"email: " + this.email + "\n" +
				"phone number: " + this.phoneNumber + "\n" +
				"address: " + this.address + "\n" +
				"user_id: " + this.userId + "\n" +
				"name: " + this.name + "\n";
	}
}
