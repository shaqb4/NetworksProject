package com.aws.codestar.projecttemplates.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.Address;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class AddressHandler implements RequestHandler<Map<String, Object>, Object> {

	@Override
	public Object handleRequest(Map<String, Object> event, Context context) {
		Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        int status = 200;
        
        JSONObject responseBody = new JSONObject();
        
        String method = (String) event.get("httpMethod");
        if (method.compareToIgnoreCase("POST") != 0) {
        	responseBody.put("mess", "Bad http request: " + method);
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        
        String body = (String) event.get("body");
        System.out.println(body);
        
        Address addr = new Address();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        String resp = "{\"mess\": \"Invalid data\"}";
        try {
        	System.out.println("before addr map");
			addr = mapper.readValue(body, Address.class);
			System.out.println("after addr map");
			resp = mapper.writeValueAsString(addr);
			System.out.println("after resp map");			
			responseBody = new JSONObject(resp);
			System.out.println("after responseBody map");
		} catch (IOException e) {
			resp = "{\"mess\": \"" + e.getMessage() + "\"}";
		}
        
        System.out.println(responseBody.toString());
        
		return new GatewayResponse(responseBody.toString(), headers, status);
	}
}
