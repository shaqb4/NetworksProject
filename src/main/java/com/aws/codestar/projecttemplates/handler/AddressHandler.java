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
        	responseBody.put("mess", "Insertion failed");
        	status = 500;
        	return new GatewayResponse(responseBody.toString(), headers, status);
        }
        
        String dbUrl = this.getDBUrl(dbName, dbUser, dbPwd, dbHost, dbPort);
        
        System.out.println(dbUrl);
        
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
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        		responseBody.put("mess", "A name, street and user id are required to create an address");
        	}
        	break;
        case "get":
        	try {
        		List<Address> addresses = jdbi.withExtension(AddressDao.class, dao -> {
        			return dao.listAdressesByUserId(pathParams.getLong("user_id"));
        		});
        		
        		JSONArray addrJson = new JSONArray(addresses);
        		responseBody.put("address", addrJson);
        	} catch (JSONException e) {
        		System.out.println(e.getMessage());
        		responseBody.put("mess", "An Id is needed to retrieve an address");
        	}
        	break;
        case "put":
        	break;
        case "delete":
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
	/*
	public static void main(String[] args) {
		List<Address> addresses = new ArrayList<>();
		
		addresses.add(new Address().withUserId(1).withStreet("9 Sunshine Ct.").withName("Home").withId(29));
		addresses.add(new Address().withUserId(1).withStreet("1101 Stanford Dr.").withName("School").withId(30));
		
		JSONArray json = new JSONArray(addresses);
		
		System.out.println(json.toString());
	}*/
}
