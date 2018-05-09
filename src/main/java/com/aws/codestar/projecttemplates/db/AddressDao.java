package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.Address;
import com.aws.codestar.projecttemplates.model.User;

public interface AddressDao {
	@SqlUpdate("INSERT INTO addresses(street, city, state, zip, country, name, user_id) VALUES (:street, :city, :state, :zip, :country, :name, :userId)")
	@GetGeneratedKeys
	@RegisterBeanMapper(Address.class)
	public Address insertAddress(@BindBean Address address);
	
	@SqlQuery("SELECT * FROM addresses WHERE id = :id")
	@RegisterBeanMapper(Address.class)
	public Address getAddressById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM addresses WHERE id = :id")
	@RegisterBeanMapper(Address.class)
	public Address getAddress(@BindBean Address address);
	
	@SqlQuery("SELECT * FROM addresses where user_id = :userId")
	@RegisterBeanMapper(Address.class)
	public List<Address> listAdressesByUserId(@Bind("userId") Long userId);

	@SqlQuery("SELECT * FROM addresses where user_id = :id")
	@RegisterBeanMapper(Address.class)
	public List<Address> listAdressesByUser(@BindBean User user);
	
	@SqlUpdate("UPDATE addresses SET street = :street, city = :city, state = :state, zip = :zip, country = :country, name = :name WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Address.class)
	public Address updateAddress(@BindBean Address address);
	
	@SqlUpdate("DELETE FROM addresses WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Address.class)
	public Address deleteAddressById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM addresses WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Address.class)
	public Address deleteAddress(@BindBean Address address);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM addresses WHERE id = :id)")
	public boolean addressExists(@Bind("id") Long id);
}
