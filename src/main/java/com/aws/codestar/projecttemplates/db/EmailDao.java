package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.Email;
import com.aws.codestar.projecttemplates.model.User;

public interface EmailDao {
	@SqlUpdate("INSERT INTO emails(email, name, user_id) VALUES (:email, :name, :userId)")
	@GetGeneratedKeys
	@RegisterBeanMapper(Email.class)
	public Email insertEmail(@BindBean Email email);
	
	@SqlQuery("SELECT * FROM emails WHERE id = :id")
	@RegisterBeanMapper(Email.class)
	public Email getEmailById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM emails WHERE id = :id")
	@RegisterBeanMapper(Email.class)
	public Email getEmail(@BindBean Email email);
	
	@SqlQuery("SELECT * FROM emails where user_id = :userId")
	@RegisterBeanMapper(Email.class)
	public List<Email> listEmailsByUserId(@Bind("userId") Long userId);

	@SqlQuery("SELECT * FROM emails where user_id = :id")
	@RegisterBeanMapper(Email.class)
	public List<Email> listEmailsByUser(@BindBean User user);
	
	@SqlUpdate("UPDATE emails SET email = :email, name = :name WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Email.class)
	public Email updateEmail(@BindBean Email email);
	
	@SqlUpdate("DELETE FROM emails WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Email.class)
	public Email deleteEmailById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM emails WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Email.class)
	public Email deleteEmail(@BindBean Email email);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM emails WHERE id = :id)")
	public boolean emailExists(@Bind("id") Long id);
}
