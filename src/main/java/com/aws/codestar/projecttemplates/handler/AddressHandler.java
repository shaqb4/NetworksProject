package com.aws.codestar.projecttemplates.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.db.AddressDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.Address;
import com.aws.codestar.projecttemplates.model.User;

public class AddressHandler implements RequestHandler<Map<String, Object>, Object> {

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
    		Address addr = new Address()
            		.withStreet(reqBody.getString("street"))
            		.withName(reqBody.getString("name"))
            		.withUserId(pathParams.getLong("user_id"))
            		.withCity(reqBody.optString("city"))
            		.withState(reqBody.optString("state"))
            		.withZip(reqBody.optString("zip"))
            		.withCountry(reqBody.optString("country"));
    		
    		Address address = this.jdbi.withExtension(AddressDao.class, dao -> 
    			dao.insertAddress(addr)
    		);

    		JSONObject addrJson = new JSONObject(address);
    		this.responseBody.put("address", addrJson);
    		this.status = 201;
    		this.headers.put("Location", this.path + "/" + address.getId());
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A name, street and user id are required to create an address");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGetList(JSONObject pathParams) {
		try {
    		List<Address> addresses = this.jdbi.withExtension(AddressDao.class, dao -> 
    			dao.listAdressesByUserId(pathParams.getLong("user_id"))
    		);
    		
    		JSONArray addrJson = new JSONArray(addresses);
    		this.responseBody.put("address", addrJson);
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A user ID is needed to retrieve a user's addresses");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleGet(JSONObject pathParams) {
		try {
    		Address address = this.jdbi.withExtension(AddressDao.class, dao -> 
    			dao.getAddressById(pathParams.getLong("id"))
    		);
    		
    		if (address != null) {
    			JSONObject addrJson = new JSONObject(address);
        		this.responseBody.put("address", addrJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Address not found");
    		}
    		
    		
    	} catch (JSONException e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An address ID is needed to retrieve an address");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handlePut(JSONObject reqBody, JSONObject pathParams) {
		try {
    		Address exisitingAddr = this.jdbi.withExtension(AddressDao.class, dao -> 
    			dao.getAddressById(pathParams.getLong("id"))
    		);
    		
    		if (exisitingAddr != null) {
        		Address newAddr = new Address()
        				.withId(pathParams.getLong("id"))
        				.withUserId(exisitingAddr.getUserId()) //Can't update user id
        				.withStreet(reqBody.optString("street", exisitingAddr.getStreet()))
                		.withName(reqBody.optString("name", exisitingAddr.getName()))
                		.withCity(reqBody.optString("city", exisitingAddr.getCity()))
                		.withState(reqBody.optString("state", exisitingAddr.getState()))
                		.withZip(reqBody.optString("zip", exisitingAddr.getZip()))
                		.withCountry(reqBody.optString("country", exisitingAddr.getCountry()));
        		
        		Address address = this.jdbi.withExtension(AddressDao.class, dao -> 
        			dao.updateAddress(newAddr)
        		);

        		JSONObject addrJson = new JSONObject(address);
        		this.responseBody.put("address", addrJson);
    		} else {
    			this.status = 404;
    			this.responseBody.put("mess", "Address not found");
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An address ID is required to update an address");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private GatewayResponse handleDelete(JSONObject pathParams) {
		try {        		
    		boolean addressExists = this.jdbi.withExtension(AddressDao.class, dao ->
    			dao.addressExists(pathParams.getLong("id"))
    		);
    		
    		if (addressExists) {
	        	Address address = this.jdbi.withExtension(AddressDao.class, dao ->
	    			dao.deleteAddressById(pathParams.getLong("id"))
	    		);
	        	
	        	JSONObject addrJson = new JSONObject(address);
	    		this.responseBody.put("address", addrJson);
    		} else {
    			this.status = 204;
    		}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "An address ID is required to delete an address");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
}
