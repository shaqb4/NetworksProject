package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.db.ProfileDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.Profile;
import com.aws.codestar.projecttemplates.model.User;

public class UserHandler implements RequestHandler<Map<String, Object>, Object> {
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
        String username = reqBody.getString("username");
        
        boolean userExists = this.jdbi.withExtension(UserDao.class, dao -> 
	    	dao.userExists(username)
		);
	    
	    if (userExists) {
	    	this.responseBody.put("mess", "User already exists");
	    	this.status = 404;
	    	return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	    }
        
        GatewayResponse response = null;
        
        switch (method.toLowerCase()) {
        case "post":
        	if (path.toLowerCase().equals("/user")) {
        		response = this.handlePost(reqBody, pathParams);
        	}
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
    		User user = new User()
            		.withFirst(reqBody.getString("first"))
            		.withLast(reqBody.getString("last"))
            		.withUsername(reqBody.getString("username"))
            		.withPassword(BCrypt.hashpw(reqBody.getString("password"), BCrypt.gensalt()));
            		
    		
    		if (!user.getPassword().isEmpty()) {
        		User newUser = this.jdbi.withExtension(UserDao.class, dao -> 
	    			dao.insertUser(user)
	    		);
	
	    		JSONObject userJson = new JSONObject(newUser);
	    		this.responseBody.put("user", userJson);
	    		this.status = 201;
	    		this.headers.put("Location", this.path + "/" + newUser.getId());
    		} else {
        		this.status = 400;
        		this.responseBody.put("mess", "Need first name, last name, username and passoword to create a user");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "Need first name, last name, username and passoword to create a user");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
}
