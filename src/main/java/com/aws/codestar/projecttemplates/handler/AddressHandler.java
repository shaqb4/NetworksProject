package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
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
        
        System.out.println("Before env vars");
        
        String dbName = System.getenv().get("database");
        String dbUser = System.getenv().get("user");
        String dbPwd = System.getenv().get("password");
        String dbHost = System.getenv().get("host");
        String dbPort = System.getenv().get("port");
        
        System.out.println("after env vars");
        
        JSONObject responseBody = new JSONObject();
        
        if (dbName == null || dbUser == null || dbPwd == null || dbHost == null || dbPort == null) {
        	responseBody.put("mess", "Insertion failed");
        	status = 500;
        	return new GatewayResponse(responseBody.toString(), headers, status);
        }
        
        System.out.println("After null check for env vars");
        
        String dbUrl = this.getDBUrl(dbName, dbUser, dbPwd, dbHost, dbPort);
        
        System.out.println("After dbURL");
        System.out.println(dbUrl);
        
        Jdbi jdbi = Jdbi.create(dbUrl);
        System.out.println("Created jdbi");
        
		jdbi.installPlugin(new SqlObjectPlugin());
		System.out.println("Added SqlObject plugin");
        
        String method = (String) event.get("httpMethod");
        
        JSONObject reqBody = new JSONObject((String) event.get("body"));
        switch (method.toLowerCase()) {
        case "post":
        	try {
        		System.out.println("Before address from body");
        		Address addr = new Address()
                		.withStreet(reqBody.getString("street"))
                		.withName(reqBody.getString("name"))
                		.withUserId(reqBody.getLong("user_id"))
                		.withCity(reqBody.optString("city"))
                		.withState(reqBody.optString("state"))
                		.withZip(reqBody.optString("zip"))
                		.withCountry(reqBody.optString("country"));
        		
        		System.out.println("After address from json");
        		System.out.println(addr);
        		
        		Address address = jdbi.withExtension(AddressDao.class, dao -> {
        			System.out.println("inserting");
        			return dao.insertAddress(addr);
        		});
        		
        		System.out.println("After insert");
        		JSONObject addrJson = new JSONObject(address);
        		System.out.println("After json from address bean");
        		responseBody.put("address", addrJson);
        		System.out.println("After response body put");
        		
        	} catch (JSONException e) {
        		System.out.println(e.getMessage());
        		
        		responseBody.put("mess", "A name, street and user id are required to create an address");
        	}
        	break;
        case "get":
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
	
	/*public static void main(String[] args) {
		Address addr = new Address();
		//addr.setId(3);
		addr.setStreet("9 Sunshine Ct.");
		addr.setUserId(1);
		addr.setName("Home address");
		addr.setCity("Nashua");
		addr.setState("New Hampshire");
		addr.setCountry("USA");
		addr.setZip("03063");
		
		Jdbi jdbi = Jdbi.create("jdbc:postgresql://networksdb.cwzebkquvxak.us-east-1.rds.amazonaws.com:3306/networks?user=shaq&password=umfinaldb12");
		jdbi.installPlugin(new SqlObjectPlugin());
		
//		Address address = jdbi.withExtension(AddressDao.class, dao -> {
//			return dao.insertAddress(addr);
//		});
		
//		Address address = jdbi.withExtension(AddressDao.class, dao -> {
//			return dao.updateAddress(addr);
//		});
		
		List<Address> addresses = jdbi.withExtension(AddressDao.class, dao -> {
			return dao.listAdressesByUser(new User().withId(1));
		});
		
		//System.out.println(address);
		
		for (Address a : addresses) {
			System.out.println(a);
		}
	}*/
}
