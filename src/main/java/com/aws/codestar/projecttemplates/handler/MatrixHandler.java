package com.aws.codestar.projecttemplates.handler;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;

public class MatrixHandler implements RequestHandler<Map<String, Integer>, Object> {
	
	public Object handleRequest(Map<String, Integer> params, final Context context) {
    	Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        JSONObject responseBody = new JSONObject();//.put("row1", "1|2|3|4|5");
        File dataFile = new File("src/main/resources/data.txt");
        
        /*Map<String, Integer> params = Arrays.stream(input.split("&"))
        		.map(param -> param.split("="))
        		.collect(
        			Collectors.toMap(param -> param[0], param -> Integer.parseInt(param[1]))
        		);*/
        
        if (params.get("col1") != params.get("row2")) {
        	responseBody.put("mess", "“incompatible	matrices");
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        if (!dataFile.isFile()) {
        	responseBody.put("mess", "The data file does not exist");
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        return new GatewayResponse(responseBody.put("params", params.toString()).toString(), headers, 200);
        
        
        
        
    }
	
	public void multiply() {
		
	}
}
