package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.db.EmailDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.Email;

public class EmailHandler implements RequestHandler<Map<String, Object>, Object> {
	private int status;
	private JSONObject responseBody;
	private Map<String, String> headers;
	private Jdbi jdbi;
	private String path;
	
	@Override
	public Object handleRequest(Map<String, Object> event, Context context) {
		this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
        
        this.status = 200;
        this.responseBody = new JSONObject();
        
        String dbName = System.getenv().get("database");
        String dbUser = System.getenv().get("user");
        String dbPwd = System.getenv().get("password");
        String dbHost = System.getenv().get("host");
        String dbPort = System.getenv().get("port");
        
        if (dbName == null || dbUser == null || dbPwd == null || dbHost == null || dbPort == null) {
        	this.responseBody.put("mess", "Internal Server Error");
        	this.status = 500;
        	return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
        }
        
        String dbUrl = this.getDBUrl(dbName, dbUser, dbPwd, dbHost, dbPort);
        
        this.jdbi = Jdbi.create(dbUrl);
        
		this.jdbi.installPlugin(new SqlObjectPlugin());
		
        this.path = (String) event.get("path");
		
		String method = (String) event.get("httpMethod");
        
        
        
        JSONObject pathParams = new JSONObject((LinkedHashMap<String, String>) event.get("pathParameters"));
        
        JSONObject reqBody = null;
        try {
        	reqBody = new JSONObject((String) event.get("body"));
        } catch(Exception e) {
        	System.out.println("Could not get reqBody");
        }
        
        boolean userExists = this.jdbi.withExtension(UserDao.class, dao -> 
        	dao.userExists(pathParams.getLong("user_id"))
		);
        
        if (!userExists) {
        	this.responseBody.put("mess", "User not found");
        	this.status = 404;
        	return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
        }
        
        GatewayResponse response = null;
        
        switch (method.toLowerCase()) {
        case "post":
        	response = this.handlePost(reqBody, pathParams);
        	break;
        case "get":
        	if (pathParams.optLong("id", -1) == -1) {
	        	response = this.handleGetList(pathParams);
        	} else {
        		response = this.handleGet(pathParams);
        	}
        	break;
        case "put":
        	response = this.handlePut(reqBody, pathParams);
        	break;
        case "delete":
        	response = this.handleDelete(pathParams);
        	break;
        default:
        	responseBody.put("mess", "Bad http request: " + method);
        	response = new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
        }
        
        System.out.println(responseBody.toString());
		return response;
	}
	
	private String getDBUrl(String dbName, String dbUser, String dbPwd, String dbHost, String dbPort) {
		StringBuilder sb = new StringBuilder()
        		.append("jdbc:postgresql://")
        		.append(dbHost)
        		.append(':')
        		.append(dbPort)
        		.append('/')
        		.append(dbName)
        		.append("?user=")
        		.append(dbUser)
        		.append("&password=")
        		.append(dbPwd);
		
		return sb.toString();
	}
	
	private GatewayResponse handlePost(JSONObject reqBody, JSONObject pathParams) {
		try {
    		Email email = new Email()
            		.withEmail(reqBody.getString("email"))
            		.withName(reqBody.getString("name"))
            		.withUserId(pathParams.getLong("user_id"));
    		
    		Email newEmail = this.jdbi.withExtension(EmailDao.class, dao -> 
    			dao.insertEmail(email)
    		);

    		JSONObject emailJson = new JSONObject(newEmail);
    		this.responseBody.put("email", emailJson);
    		this.status = 201;
    		this.headers.put("Location", this.path + "/" + newEmail.getId());
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A name, email and user id are required to create an email");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGetList(JSONObject pathParams) {
		try {
    		List<Email> emails = this.jdbi.withExtension(EmailDao.class, dao -> 
    			dao.listEmailsByUserId(pathParams.getLong("user_id"))
    		);
    		
    		JSONArray emailJson = new JSONArray(emails);
    		this.responseBody.put("email", emailJson);
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A user ID is needed to retrieve a user's email");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGet(JSONObject pathParams) {
		try {
    		Email email = this.jdbi.withExtension(EmailDao.class, dao -> 
    			dao.getEmailById(pathParams.getLong("id"))
    		);
    		
    		if (email != null) {
    			JSONObject emailJson = new JSONObject(email);
        		this.responseBody.put("email", emailJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Email not found");
    		}
    		
    		
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An email ID is needed to retrieve an email");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handlePut(JSONObject reqBody, JSONObject pathParams) {
		try {
    		Email exisitingEmail = this.jdbi.withExtension(EmailDao.class, dao -> 
    			dao.getEmailById(pathParams.getLong("id"))
    		);
    		
    		if (exisitingEmail != null) {
        		Email newEmail = new Email()
        				.withId(pathParams.getLong("id"))
        				.withUserId(exisitingEmail.getUserId()) //Can't update user id
        				.withEmail(reqBody.optString("email", exisitingEmail.getEmail()))
                		.withName(reqBody.optString("name", exisitingEmail.getName()));
                		
        		
        		Email email = this.jdbi.withExtension(EmailDao.class, dao -> 
        			dao.updateEmail(newEmail)
        		);

        		JSONObject emailJson = new JSONObject(email);
        		this.responseBody.put("email", emailJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Email not found");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An email ID is required to update an email");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleDelete(JSONObject pathParams) {
		try {        		
    		boolean emailExists = this.jdbi.withExtension(EmailDao.class, dao ->
    			dao.emailExists(pathParams.getLong("id"))
    		);
    		
    		if (emailExists) {
	        	Email email = this.jdbi.withExtension(EmailDao.class, dao ->
	    			dao.deleteEmailById(pathParams.getLong("id"))
	    		);
	        	
	        	JSONObject emailJson = new JSONObject(email);
	    		this.responseBody.put("email", emailJson);
    		} else {
    			this.status = 204;
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An email ID is required to delete an email");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
}