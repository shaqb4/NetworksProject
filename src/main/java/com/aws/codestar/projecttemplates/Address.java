package com.aws.codestar.projecttemplates;

import java.util.Optional;

public class Address {
	private Optional<Integer> id;
	private Optional<String> street;
	private Optional<String> city;
	private Optional<String> state;
	private Optional<String> zip;
	
	
	public Address() {
		this.id = Optional.empty();
		this.street = Optional.empty();
		this.city = Optional.empty();
		this.state = Optional.empty();
		this.zip = Optional.empty();
	}
	
	public Address(Optional<Integer> id, Optional<String> street, Optional<String> city, Optional<String> state, Optional<String> zip) {
		super();
		this.id = id;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
	
	public Address(Optional<String> street, Optional<String> city, Optional<String> state, Optional<String> zip) {
		super();
		this.id = Optional.empty();
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
	
	public Optional<String> getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = Optional.of(street);
	}
	public Optional<String> getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = Optional.of(city);
	}
	public Optional<String> getState() {
		return state;
	}
	public void setState(String state) {
		this.state = Optional.of(state);
	}
	public Optional<String> getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = Optional.of(zip);
	}

	public Optional<Integer> getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = Optional.of(id);
	}
}
