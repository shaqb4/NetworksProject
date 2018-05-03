package com.aws.codestar.projecttemplates.db;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface UserDao {
	@SqlQuery("SELECT EXISTS(SELECT true FROM users WHERE id = :id)")
	public boolean userExists(@Bind("id") long id);
}
