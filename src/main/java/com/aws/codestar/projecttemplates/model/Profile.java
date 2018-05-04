package com.aws.codestar.projecttemplates.model;

public class Profile {

	private long id;
	
	private long email;
	
	private long phoneNumber;
	
	private long address;
	
	private long userId;
	
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Profile withId(long id) {
		this.id = id;
		return this;
	}

	public long getEmail() {
		return email;
	}

	public void setEmail(long email) {
		this.email = email;
	}

	public Profile withEmail(long email) {
		this.email = email;
		return this;
	}
	
	public long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Profile withPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}
	
	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}

	public Profile withAddress(long address) {
		this.address = address;
		return this;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long user_id) {
		this.userId = user_id;
	}
	
	public Profile withUserId(long user_id) {
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
