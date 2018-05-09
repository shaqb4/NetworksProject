package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.PhoneNumber;
import com.aws.codestar.projecttemplates.model.User;

public interface PhoneNumberDao {
	@SqlUpdate("INSERT INTO phone_numbers(number, name, user_id) VALUES (:number, :name, :userId)")
	@GetGeneratedKeys
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber insertPhoneNumber(@BindBean PhoneNumber phoneNumber);
	
	@SqlQuery("SELECT * FROM phone_numbers WHERE id = :id")
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber getPhoneNumberById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM phone_numbers WHERE id = :id")
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber getPhoneNumber(@BindBean PhoneNumber phoneNumber);
	
	@SqlQuery("SELECT * FROM phone_numbers where user_id = :userId")
	@RegisterBeanMapper(PhoneNumber.class)
	public List<PhoneNumber> listPhoneNumbersByUserId(@Bind("userId") Long userId);

	@SqlQuery("SELECT * FROM phone_numbers where user_id = :id")
	@RegisterBeanMapper(PhoneNumber.class)
	public List<PhoneNumber> listPhoneNumbersByUser(@BindBean User user);
	
	@SqlUpdate("UPDATE phone_numbers SET number = :number, name = :name WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber updatePhoneNumber(@BindBean PhoneNumber phoneNumber);
	
	@SqlUpdate("DELETE FROM phone_numbers WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber deletePhoneNumberById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM phone_numbers WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(PhoneNumber.class)
	public PhoneNumber deletePhoneNumber(@BindBean PhoneNumber phoneNumber);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM phone_numbers WHERE id = :id)")
	public boolean phoneNumberExists(@Bind("id") Long id);
}
