package com.aws.codestar.projecttemplates.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.codestar.projecttemplates.GatewayResponse;

public class MatrixHandler implements RequestHandler<Map<String, Object>, Object> {
	
	public Object handleRequest(Map<String, Object> event, final Context context) {
    	Map<String, String> headers = new HashMap<>();
    	int status = 200;
        headers.put("Content-Type", "application/json");
        
        JSONObject responseBody = new JSONObject();
        //File dataFile = new File("src/main/resources/data.txt");
        ClassLoader classLoader = getClass().getClassLoader();
    	File dataFile = new File(classLoader.getResource("data.txt").getFile());
        
        String method = (String) event.get("httpMethod");
        if (method.compareToIgnoreCase("POST") != 0) {
        	responseBody.put("mess", "Bad http request: " + method);
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        String body = (String) event.get("body");
        Map<String, Integer> params = Arrays.stream(body.split("&"))
        		.map(param -> param.split("="))
        		.collect(
        			Collectors.toMap(param -> param[0], param -> Integer.parseInt(param[1]))
        		);
        
        if (params.get("col1") != params.get("row2")) {
        	responseBody.put("mess", "incompatible	matrices");
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        int numValues = params.get("row1") * params.get("col1") + params.get("row2") * params.get("col2");
        
        if (params.get("startline") - 1 + numValues >= 1000) {
        	responseBody.put("mess", "matrices too large");
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        if (!dataFile.isFile()) {
        	responseBody.put("mess", "The data file does not exist");
        	return new GatewayResponse(responseBody.toString(), headers, 200);
        }
        
        try (BufferedReader br = Files.newBufferedReader(dataFile.toPath())) {
        	
        	Integer[] values = br.lines()
        		.skip(params.get("startline") - 1)
        		.map(val -> Integer.parseInt(val))
        		.toArray(Integer[]::new);
        	int row1 = params.get("row1");
        	int col1 = params.get("col1");
        	int row2 = params.get("row2");
        	int col2 = params.get("col2");
        	Integer[] mat1 = Arrays.copyOfRange(values, 0, row1 * col1);
        	Integer[] mat2 = Arrays.copyOfRange(values, row1 * col1, values.length);
        	
        	Integer[] mat3 = multiply(mat1, mat2, row1, col1, row2, col2);
        	
        	StringBuilder sb = new StringBuilder();
        	for (int i = 0; i < row1; i++) {
        		sb.append(mat3[i * col2]);
        		for (int j = 1; j < col2; j++) {
        			sb.append("|" + mat3[i * col2 + j]);
        		}
        		responseBody.put("row" + (i+1), sb.toString());
        		sb.delete(0, sb.length());
        	}
        	
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
        
        return new GatewayResponse(responseBody.toString(), headers, status);
        
        
        
        
    }
	
	public Integer[] multiply(Integer[] mat1, Integer[] mat2, int row1, int col1, int row2, int col2) {
		Integer[] mat3 = new Integer[row1 * col2];
		for (int i = 0; i < row1; i++) {
			for (int j = 0; j < col2; j++) {
				int index = i * col2 + j; 
				mat3[index] = 0;
				for (int k = 0; k < row2; k++) {
					mat3[index] += mat1[i * col1 + k] * mat2[k * col2 + j];
				}
			}
		}
		
		return mat3;
	}
}
