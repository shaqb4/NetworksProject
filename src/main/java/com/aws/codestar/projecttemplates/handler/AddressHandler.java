package com.aws.codestar.projecttemplates.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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
        context.getLogger().log("Body is : " + body);
        
        Address addr = new Address();
        
        context.getLogger().log("Empty address made");
        
        ObjectMapper mapper = new ObjectMapper();
        context.getLogger().log("Objct mapper made");
        mapper.registerModule(new Jdk8Module());
        context.getLogger().log("Jdk8 module registered");
        String resp = "{\"mess\": \"Invalid data\"}";
        try {
        	context.getLogger().log("before addr map");
			addr = mapper.readValue(body, Address.class);
			context.getLogger().log("after addr map");
			resp = mapper.writeValueAsString(addr);
			context.getLogger().log("after resp map");			
			responseBody = new JSONObject(resp);
			context.getLogger().log("after responseBody map");
		} catch (IOException e) {
			resp = "{\"mess\": \"" + e.getMessage() + "\"}";
		}
        
        System.out.println(responseBody.toString());
        
		return new GatewayResponse(responseBody.toString(), headers, status);
	}
}
