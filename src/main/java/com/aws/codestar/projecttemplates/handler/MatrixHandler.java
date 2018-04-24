package com.aws.codestar.projecttemplates.handler;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;

public class MatrixHandler implements RequestHandler<Object, Object> {
	public Object handleRequest(final Object input, final Context context) {
    	Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        JSONObject responseBody = new JSONObject().put("row1", "1|2|3|4|5");
        
        return new GatewayResponse(responseBody.toString(), headers, 200);
    }
}
