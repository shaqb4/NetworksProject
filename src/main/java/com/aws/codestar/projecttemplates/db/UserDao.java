package com.aws.codestar.projecttemplates.db;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.User;

public interface UserDao {
	@SqlUpdate("INSERT INTO users(first, last, username, password) VALUES (:first, :last, :username, :password)")
	@GetGeneratedKeys
	@RegisterBeanMapper(User.class)
	public User insertUser(@BindBean User user);
	
	@SqlQuery("SELECT * FROM users WHERE id = :id")
	@RegisterBeanMapper(User.class)
	public User getUserById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM users WHERE username = :username")
	@RegisterBeanMapper(User.class)
	public User getUserByUsername(@Bind("username") String username);
	
	@SqlQuery("SELECT * FROM users WHERE id = :id")
	@RegisterBeanMapper(User.class)
	public User getUser(@BindBean User user);
	
	@SqlUpdate("UPDATE users SET first = :first, last = :last WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(User.class)
	public User updateUser(@BindBean User user);
	
	@SqlUpdate("DELETE FROM users WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(User.class)
	public User deleteUserById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM users WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(User.class)
	public User deleteUser(@BindBean User user);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM users WHERE id = :id)")
	public boolean userExists(@Bind("id") Long id);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM users WHERE username = :username)")
	public boolean userExists(@Bind("username") String username);
}
