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
import com.aws.codestar.projecttemplates.db.PhoneNumberDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.PhoneNumber;

public class PhoneNumberHandler implements RequestHandler<Map<String, Object>, Object> {
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
    		PhoneNumber phoneNumber = new PhoneNumber()
            		.withNumber(reqBody.getString("number"))
            		.withName(reqBody.getString("name"))
            		.withUserId(pathParams.getLong("user_id"));
    		
    		PhoneNumber newPhoneNumber = this.jdbi.withExtension(PhoneNumberDao.class, dao -> 
    			dao.insertPhoneNumber(phoneNumber)
    		);

    		JSONObject phoneNumberJson = new JSONObject(newPhoneNumber);
    		this.responseBody.put("phone_number", phoneNumberJson);
    		this.status = 201;
    		this.headers.put("Location", this.path + "/" + newPhoneNumber.getId());
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A name, phone number and user id are required to create an phone number");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGetList(JSONObject pathParams) {
		try {
    		List<PhoneNumber> phoneNumbers = this.jdbi.withExtension(PhoneNumberDao.class, dao -> 
    			dao.listPhoneNumbersByUserId(pathParams.getLong("user_id"))
    		);
    		
    		JSONArray phoneNumberJson = new JSONArray(phoneNumbers);
    		this.responseBody.put("phone_number", phoneNumberJson);
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A user ID is needed to retrieve a user's phone number");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGet(JSONObject pathParams) {
		try {
    		PhoneNumber phoneNumber = this.jdbi.withExtension(PhoneNumberDao.class, dao -> 
    			dao.getPhoneNumberById(pathParams.getLong("id"))
    		);
    		
    		if (phoneNumber != null) {
    			JSONObject phoneNumberJson = new JSONObject(phoneNumber);
        		this.responseBody.put("phone_number", phoneNumberJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Phone number not found");
    		}
    		
    		
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A number ID is needed to retrieve a phone number");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handlePut(JSONObject reqBody, JSONObject pathParams) {
		try {
    		PhoneNumber exisitingPhoneNumber = this.jdbi.withExtension(PhoneNumberDao.class, dao -> 
    			dao.getPhoneNumberById(pathParams.getLong("id"))
    		);
    		
    		if (exisitingPhoneNumber != null) {
        		PhoneNumber newPhoneNumber = new PhoneNumber()
        				.withId(pathParams.getLong("id"))
        				.withUserId(exisitingPhoneNumber.getUserId()) //Can't update user id
        				.withNumber(reqBody.optString("number", exisitingPhoneNumber.getNumber()))
                		.withName(reqBody.optString("name", exisitingPhoneNumber.getName()));
                		
        		
        		PhoneNumber phoneNumber = this.jdbi.withExtension(PhoneNumberDao.class, dao -> 
        			dao.updatePhoneNumber(newPhoneNumber)
        		);

        		JSONObject phoneNumberJson = new JSONObject(phoneNumber);
        		this.responseBody.put("phone_number", phoneNumberJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Phone number not found");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A number ID is required to update an phone number");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleDelete(JSONObject pathParams) {
		try {        		
    		boolean phoneNumberExists = this.jdbi.withExtension(PhoneNumberDao.class, dao ->
    			dao.phoneNumberExists(pathParams.getLong("id"))
    		);
    		
    		if (phoneNumberExists) {
	        	PhoneNumber phoneNumber = this.jdbi.withExtension(PhoneNumberDao.class, dao ->
	    			dao.deletePhoneNumberById(pathParams.getLong("id"))
	    		);
	        	
	        	JSONObject phoneNumberJson = new JSONObject(phoneNumber);
	    		this.responseBody.put("phone_number", phoneNumberJson);
    		} else {
    			this.status = 204;
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A number ID is required to delete an phone number");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
}
