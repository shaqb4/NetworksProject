package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.AddressRequest;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddressHandler implements RequestHandler<AddressRequest, Object> {

	@Override
	public Object handleRequest(AddressRequest address, Context context) {
		Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        int status = 200;
        
        ObjectMapper mapper = new ObjectMapper();
        String addrStr = "{\"mess\": \"Json not read\"}";
		try {
			addrStr = mapper.writeValueAsString(address);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return new GatewayResponse(addrStr, headers, status);
	}

}
