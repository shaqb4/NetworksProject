package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.Permission;
import com.aws.codestar.projecttemplates.model.User;

public interface PermissionDao {
	@SqlUpdate("INSERT INTO permissions(user_id, client, profile, email, phone_number, address, access_token) VALUES (:userId, :client, :profile, :email, :phoneNumber, :address, :accessToken)")
	@GetGeneratedKeys
	@RegisterBeanMapper(Permission.class)
	public Permission insertPermission(@BindBean Permission permission);
	
	@SqlQuery("SELECT * FROM permissions WHERE id = :id")
	@RegisterBeanMapper(Permission.class)
	public Permission getPermissionById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM permissions WHERE id = :id")
	@RegisterBeanMapper(Permission.class)
	public Permission getPermission(@BindBean Permission permission);
	
	@SqlQuery("SELECT * FROM permissions where user_id = :userId")
	@RegisterBeanMapper(Permission.class)
	public List<Permission> listAdressesByUserId(@Bind("userId") Long userId);

	@SqlQuery("SELECT * FROM permissions where user_id = :id")
	@RegisterBeanMapper(Permission.class)
	public List<Permission> listAdressesByUser(@BindBean User user);
	
	@SqlUpdate("UPDATE permissions SET client = :client, profile = :profile, email = :email, phone_number = :phoneNumber, address = :address, access_token = :accessToken WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Permission.class)
	public Permission updatePermission(@BindBean Permission permission);
	
	@SqlUpdate("DELETE FROM permissions WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Permission.class)
	public Permission deletePermissionById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM permissions WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Permission.class)
	public Permission deletePermission(@BindBean Permission permission);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM permissions WHERE id = :id)")
	public boolean permissionExists(@Bind("id") Long id);
}
