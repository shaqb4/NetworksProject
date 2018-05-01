package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
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
        
        
        JSONObject responseBody = new JSONObject();
        
        String method = (String) event.get("httpMethod");
        
        //String body = (String) event.get("body");
        JSONObject reqBody = new JSONObject((String) event.get("body"));
        switch (method.toLowerCase()) {
        case "post":
        	/*Address addr = new Address()
        		.withStreet(street)*/
        	responseBody = reqBody;
        	break;
        case "get":
        	break;
        case "put":
        	break;
        case "delete":
        	break;
        default:
        	responseBody.put("mess", "Bad http request: " + method);
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
		return new GatewayResponse(responseBody.toString(), headers, status);
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
