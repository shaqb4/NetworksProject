package com.aws.codestar.projecttemplates.model;

public class Address {
	private Long id;
	
	private String street;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String zip;
	
	private Long userId;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Address withId(Long id) {
		this.id = id;
		return this;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Address withStreet(String street) {
		this.street = street;
		return this;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Address withCity(String city) {
		this.city = city;
		return this;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public Address withState(String state) {
		this.state = state;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public Address withCountry(String country) {
		this.country = country;
		return this;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public Address withZip(String zip) {
		this.zip = zip;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long user_id) {
		this.userId = user_id;
	}
	
	public Address withUserId(Long user_id) {
		this.userId = user_id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Address withName(String name) {
		this.name = name;
		return this;
	}
	
	public String toString() {
		return 	"id: " + this.id  + "\n" +
				"street: " + this.street+ "\n" +
				"city: " + this.city+ "\n" +
				"state: " + this.state  + "\n" +
				"zip: " + this.zip  + "\n" +
				"country: " + this.country+ "\n" +
				"user_id: " + this.userId  + "\n" +
				"name: " + this.name  + "\n";
	}
	
}
