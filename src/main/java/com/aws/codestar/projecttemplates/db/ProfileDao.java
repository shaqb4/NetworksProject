package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.Profile;
import com.aws.codestar.projecttemplates.model.User;

public interface ProfileDao {
	@SqlUpdate("INSERT INTO profiles(address, email, phone_number, name, user_id) VALUES (:address, :email, :phoneNumber, :name, :userId)")
	@GetGeneratedKeys
	@RegisterBeanMapper(Profile.class)
	public Profile insertProfile(@BindBean Profile profile);
	
	@SqlQuery("SELECT * FROM profiles WHERE id = :id")
	@RegisterBeanMapper(Profile.class)
	public Profile getProfileById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM profiles WHERE id = :id")
	@RegisterBeanMapper(Profile.class)
	public Profile getProfile(@BindBean Profile profile);
	
	@SqlQuery("SELECT * FROM profiles where user_id = :userId")
	@RegisterBeanMapper(Profile.class)
	public List<Profile> listProfilesByUserId(@Bind("userId") Long userId);

	@SqlQuery("SELECT * FROM profiles where user_id = :id")
	@RegisterBeanMapper(Profile.class)
	public List<Profile> listProfilesByUser(@BindBean User user);
	
	@SqlUpdate("UPDATE profiles SET address = :address, email = :email,  phone_number = :phoneNumber, name = :name WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Profile.class)
	public Profile updateProfile(@BindBean Profile profile);
	
	@SqlUpdate("DELETE FROM profiles WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Profile.class)
	public Profile deleteProfileById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM profiles WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Profile.class)
	public Profile deleteProfile(@BindBean Profile profile);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM profiles WHERE id = :id)")
	public boolean profileExists(@Bind("id") Long id);
}
