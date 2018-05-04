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
import com.aws.codestar.projecttemplates.db.ProfileDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.Profile;

public class ProfileHandler implements RequestHandler<Map<String, Object>, Object> {
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
    		Profile profile = new Profile()
            		.withName(reqBody.getString("name"))
            		.withUserId(pathParams.getLong("user_id"))
            		.withEmail(reqBody.optLong("email", 0))
            		.withAddress(reqBody.optLong("address", 0))
            		.withPhoneNumber(reqBody.optLong("phone_number", 0));
    		
    		if (profile.getEmail() > 0 || profile.getAddress() > 0 || profile.getPhonenNumber() > 0) {
        		Profile newProfile = this.jdbi.withExtension(ProfileDao.class, dao -> 
	    			dao.insertProfile(profile)
	    		);
	
	    		JSONObject profileJson = new JSONObject(newProfile);
	    		this.responseBody.put("profile", profileJson);
	    		this.status = 201;
	    		this.headers.put("Location", this.path + "/" + newProfile.getId());
    		} else {
        		this.status = 400;
        		this.responseBody.put("mess", "Need at least one of email id, address id, or phone number id to create a profile");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A name, one or more of email id, address id, or phone number id, and user id are required to create a profile");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGetList(JSONObject pathParams) {
		try {
    		List<Profile> profiles = this.jdbi.withExtension(ProfileDao.class, dao -> 
    			dao.listProfilesByUserId(pathParams.getLong("user_id"))
    		);
    		
    		JSONArray profileJson = new JSONArray(profiles);
    		this.responseBody.put("profile", profileJson);
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A user ID is needed to retrieve a user's profile");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGet(JSONObject pathParams) {
		try {
    		Profile profile = this.jdbi.withExtension(ProfileDao.class, dao -> 
    			dao.getProfileById(pathParams.getLong("id"))
    		);
    		
    		if (profile != null) {
    			JSONObject profileJson = new JSONObject(profile);
        		this.responseBody.put("profile", profileJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Profile not found");
    		}
    		
    		
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An profile ID is needed to retrieve an profile");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handlePut(JSONObject reqBody, JSONObject pathParams) {
		try {
    		Profile exisitingProfile = this.jdbi.withExtension(ProfileDao.class, dao -> 
    			dao.getProfileById(pathParams.getLong("id"))
    		);
    		
    		if (exisitingProfile != null) {
        		Profile newProfile = new Profile()
        				.withId(pathParams.getLong("id"))
        				.withUserId(exisitingProfile.getUserId()) //Can't update user id
        				.withEmail(reqBody.optLong("email", exisitingProfile.getEmail()))
        				.withAddress(reqBody.optLong("address", exisitingProfile.getAddress()))
        				.withPhoneNumber(reqBody.optLong("phone_number", exisitingProfile.getPhonenNumber()))
                		.withName(reqBody.optString("name", exisitingProfile.getName()));
                
        		if (newProfile.getEmail() > 0 || newProfile.getAddress() > 0 || newProfile.getPhonenNumber() > 0) {
	        		Profile profile = this.jdbi.withExtension(ProfileDao.class, dao -> 
	        			dao.updateProfile(newProfile)
	        		);
	
	        		JSONObject profileJson = new JSONObject(profile);
	        		this.responseBody.put("profile", profileJson);
        		} else {
        			this.status = 400;
            		this.responseBody.put("mess", "Need at least one of email id, address id, or phone number id to update a profile");
        		}
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Profile not found");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An profile ID is required to update an profile");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleDelete(JSONObject pathParams) {
		try {        		
    		boolean profileExists = this.jdbi.withExtension(ProfileDao.class, dao ->
    			dao.profileExists(pathParams.getLong("id"))
    		);
    		
    		if (profileExists) {
	        	Profile profile = this.jdbi.withExtension(ProfileDao.class, dao ->
	    			dao.deleteProfileById(pathParams.getLong("id"))
	    		);
	        	
	        	JSONObject profileJson = new JSONObject(profile);
	    		this.responseBody.put("profile", profileJson);
    		} else {
    			this.status = 204;
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An profile ID is required to delete an profile");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}	
}
