package com.aws.codestar.projecttemplates.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.aws.codestar.projecttemplates.model.Client;
import com.aws.codestar.projecttemplates.model.User;

public interface ClientDao {
	@SqlUpdate("INSERT INTO clients(token, name, user_id) VALUES (:token, :name, :userId)")
	@GetGeneratedKeys
	@RegisterBeanMapper(Client.class)
	public Client insertClient(@BindBean Client client);
	
	@SqlQuery("SELECT * FROM clients WHERE id = :id")
	@RegisterBeanMapper(Client.class)
	public Client getClientById(@Bind("id") Long id);
	
	@SqlQuery("SELECT * FROM clients WHERE token = :token")
	@RegisterBeanMapper(Client.class)
	public Client getClientByToken(@Bind("token") String token);
	
	@SqlQuery("SELECT * FROM clients WHERE id = :id")
	@RegisterBeanMapper(Client.class)
	public Client getClient(@BindBean Client client);
	
	@SqlUpdate("UPDATE clients SET token = :token, name = :name WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Client.class)
	public Client updateClient(@BindBean Client client);
	
	@SqlUpdate("DELETE FROM clients WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Client.class)
	public Client deleteClientById(@Bind("id") Long id);
	
	@SqlUpdate("DELETE FROM clients WHERE id = :id")
	@GetGeneratedKeys
	@RegisterBeanMapper(Client.class)
	public Client deleteClient(@BindBean Client client);
	
	@SqlQuery("SELECT EXISTS(SELECT true FROM clients WHERE id = :id)")
	public boolean clientExists(@Bind("id") Long id);
}
