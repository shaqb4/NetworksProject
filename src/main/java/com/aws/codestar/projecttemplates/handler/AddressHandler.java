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
import com.aws.codestar.projecttemplates.model.Address;
import com.aws.codestar.projecttemplates.model.User;

public class AddressHandler implements RequestHandler<Map<String, Object>, Object> {

	@Override
	public Object handleRequest(Map<String, Object> event, Context context) {
		Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        int status = 200;
        
        String dbName = System.getenv().get("database");
        String dbUser = System.getenv().get("user");
        String dbPwd = System.getenv().get("password");
        String dbHost = System.getenv().get("host");
        String dbPort = System.getenv().get("port");
        
        JSONObject responseBody = new JSONObject();
        
        if (dbName == null || dbUser == null || dbPwd == null || dbHost == null || dbPort == null) {
        	responseBody.put("mess", "Internal Server Error");
        	status = 500;
        	return new GatewayResponse(responseBody.toString(), headers, status);
        }
        
        String dbUrl = this.getDBUrl(dbName, dbUser, dbPwd, dbHost, dbPort);
        
        Jdbi jdbi = Jdbi.create(dbUrl);
        
		jdbi.installPlugin(new SqlObjectPlugin());
		
        String method = (String) event.get("httpMethod");
        
        JSONObject pathParams = new JSONObject((LinkedHashMap<String, String>) event.get("pathParameters"));
        
        JSONObject reqBody = null;
        try {
        	reqBody = new JSONObject((String) event.get("body"));
        } catch(Exception e) {
        	
        }
        
        switch (method.toLowerCase()) {
        case "post":
        	try {
        		Address addr = new Address()
                		.withStreet(reqBody.getString("street"))
                		.withName(reqBody.getString("name"))
                		.withUserId(pathParams.getLong("user_id"))
                		.withCity(reqBody.optString("city"))
                		.withState(reqBody.optString("state"))
                		.withZip(reqBody.optString("zip"))
                		.withCountry(reqBody.optString("country"));
        		
        		Address address = jdbi.withExtension(AddressDao.class, dao -> {
        			return dao.insertAddress(addr);
        		});

        		JSONObject addrJson = new JSONObject(address);
        		responseBody.put("address", addrJson);
        		status = 201;
        		headers.put("Location", (String) event.get("httpMethod") + "/" + address.getId());
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		status = 400;
        		responseBody.put("mess", "A name, street and user id are required to create an address");
        	}
        	break;
        case "get":
        	if (pathParams.optLong("id", -1) == -1) {
	        	try {
	        		List<Address> addresses = jdbi.withExtension(AddressDao.class, dao -> {
	        			return dao.listAdressesByUserId(pathParams.getLong("user_id"));
	        		});
	        		
	        		JSONArray addrJson = new JSONArray(addresses);
	        		responseBody.put("address", addrJson);
	        	} catch (JSONException e) {
	        		System.out.println(e.getMessage());
	        		status = 400;
	        		responseBody.put("mess", "A user ID is needed to retrieve a user's addresses");
	        	}
        	} else {
        		try {
	        		Address address = jdbi.withExtension(AddressDao.class, dao -> {
	        			return dao.getAddressById(pathParams.getLong("id"));
	        		});
	        		
	        		JSONObject addrJson = new JSONObject(address);
	        		responseBody.put("address", addrJson);
	        	} catch (JSONException e) {
	        		System.out.println(e.getMessage());
	        		status = 400;
	        		responseBody.put("mess", "An address ID is needed to retrieve a address");
	        	}
        	}
        	break;
        case "put":
        	try {
        		Address exisitingAddr = jdbi.withExtension(AddressDao.class, dao -> {
        			return dao.getAddressById(pathParams.getLong("id"));
        		});
        		
        		
        		Address newAddr = new Address()
        				.withId(pathParams.getLong("id"))
        				.withUserId(exisitingAddr.getUserId()) //Can't update user id
        				.withStreet(reqBody.optString("street", exisitingAddr.getStreet()))
                		.withName(reqBody.optString("name", exisitingAddr.getName()))
                		.withCity(reqBody.optString("city", exisitingAddr.getCity()))
                		.withState(reqBody.optString("state", exisitingAddr.getState()))
                		.withZip(reqBody.optString("zip", exisitingAddr.getZip()))
                		.withCountry(reqBody.optString("country", exisitingAddr.getCountry()));
        		
        		Address address = jdbi.withExtension(AddressDao.class, dao -> {
        			return dao.updateAddress(newAddr);
        		});

        		JSONObject addrJson = new JSONObject(address);
        		responseBody.put("address", addrJson);
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		status = 400;
        		responseBody.put("mess", "An address ID is required to update an address");
        	}
        	break;
        case "delete":
        	try {        		
        		boolean addressExists = jdbi.withExtension(AddressDao.class, dao -> {
	    			return dao.addressExists(pathParams.getLong("id"));
	    		});
        		
        		if (addressExists) {
		        	Address address = jdbi.withExtension(AddressDao.class, dao -> {
		    			return dao.deleteAddressById(pathParams.getLong("id"));
		    		});
		        	
		        	System.out.println("Delete: exists");
		        	
		        	JSONObject addrJson = new JSONObject(address);
		    		responseBody.put("address", addrJson);
        		} else {
        			System.out.println("Delete: doesn't exist");
        			status = 204;
        			responseBody.put("mess", "That address does not exist");
        		}
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		status = 400;
        		responseBody.put("mess", "An address ID is required to update an address");
        	}
        	break;
        default:
        	responseBody.put("mess", "Bad http request: " + method);
        	return new GatewayResponse(responseBody.toString(), headers, status);
        }
        
		return new GatewayResponse(responseBody.toString(), headers, status);
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
	
	/*public static void main(String[] args) {
		Jdbi jdbi = Jdbi.create("jdbc:postgresql://networksdb.cwzebkquvxak.us-east-1.rds.amazonaws.com:3306/networks?user=shaq&password=umfinaldb12");
		jdbi.installPlugin(new SqlObjectPlugin());
		
		boolean exists = jdbi.withExtension(AddressDao.class, dao -> {
			return dao.addressExists(102);
		});
				
		System.out.println(exists);	
	}*/
}
