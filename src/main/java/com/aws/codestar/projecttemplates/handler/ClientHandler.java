package com.aws.codestar.projecttemplates.handler;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.aws.codestar.projecttemplates.GatewayResponse;
import com.aws.codestar.projecttemplates.db.ClientDao;
import com.aws.codestar.projecttemplates.db.PermissionDao;
import com.aws.codestar.projecttemplates.db.EmailDao;
import com.aws.codestar.projecttemplates.db.UserDao;
import com.aws.codestar.projecttemplates.model.Client;
import com.aws.codestar.projecttemplates.model.Email;
import com.aws.codestar.projecttemplates.model.Permission;
import com.aws.codestar.projecttemplates.model.Profile;
import com.aws.codestar.projecttemplates.model.User;

public class ClientHandler implements RequestHandler<Map<String, Object>, Object> {
	private int status;
	private JSONObject responseBody;
	private Map<String, String> headers;
	private Jdbi jdbi;
	private String path;
	
	@Override
	public Object handleRequest(Map<String, Object> event, Context context) {
		this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
        
        this.status = 200;
        this.responseBody = new JSONObject();
        
        String dbName = System.getenv().get("database");
        String dbUser = System.getenv().get("user");
        String dbPwd = System.getenv().get("password");
        String dbHost = System.getenv().get("host");
        String dbPort = System.getenv().get("port");
        
        if (dbName == null || dbUser == null || dbPwd == null || dbHost == null || dbPort == null) {
        	this.responseBody.put("mess", "Internal Server Error");
        	this.status = 500;
        	return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
        }
        
        String dbUrl = this.getDBUrl(dbName, dbUser, dbPwd, dbHost, dbPort);
        
        this.jdbi = Jdbi.create(dbUrl);
        
		this.jdbi.installPlugin(new SqlObjectPlugin());
		
        this.path = (String) event.get("path");
		
		String method = (String) event.get("httpMethod");
        
        JSONObject pathParams = new JSONObject((LinkedHashMap<String, String>) event.get("pathParameters"));
        
        JSONObject reqBody = null;
        try {
        	reqBody = new JSONObject((String) event.get("body"));
        } catch(Exception e) {
        	System.out.println("Could not get reqBody");
        }
        
        /*boolean userExists = this.jdbi.withExtension(UserDao.class, dao -> 
	    	dao.userExists(pathParams.getLong("user_id"))
		);
	    
	    if (!userExists) {
	    	this.responseBody.put("mess", "User not found");
	    	this.status = 404;
	    	return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	    }*/
        
        GatewayResponse response = null;
        
        switch (method.toLowerCase()) {
        case "post":
        	if (path.toLowerCase().equals("/client")) {
        		response = this.handlePostCreate(reqBody, pathParams);
        	} else if (path.toLowerCase().equals("/client/token")) {
        		response = this.handlePostToken(reqBody, pathParams);
        	}
        	break;
        default:
        	responseBody.put("mess", "Bad http request: " + method);
        	response = new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
        }
        
        System.out.println(responseBody.toString());
		return response;
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
	
	private Optional<User> AuthenticateUser(String username, String password) {
		Optional<User> user = Optional.empty();
		if (username.isEmpty()) {
			return Optional.empty();
		}
		
		User existingUser = this.jdbi.withExtension(UserDao.class, dao -> 
	    	dao.getUserByUsername(username)
		);
		
		
		if (existingUser != null && existingUser.getPassword() != null && !existingUser.getPassword().isEmpty()) {
			if (BCrypt.checkpw(password, existingUser.getPassword())) {
				user = Optional.of(existingUser);
			}
		}
		
		return user;
	}
	
	private Optional<Client> AuthenticateClient(String[] token) {
		Optional<Client> client= Optional.empty();
		if (!token[0].equals("Bearer") || token[1].isEmpty()) {
			return Optional.empty();
		}
		
		Client existingClient = this.jdbi.withExtension(ClientDao.class, dao -> 
	    	dao.getClientByToken(token[1])
		);
		
		
		if (existingClient != null) {
			client = Optional.of(existingClient);
		}
		
		return client;
	}
	
	private GatewayResponse handlePostCreate(JSONObject reqBody, JSONObject pathParams) {
		try {
			Optional<User> user = this.AuthenticateUser(reqBody.getString("username"), reqBody.getString("password"));
			
			if (!user.isPresent()) {
				System.out.println("User not authenticated");
	    		this.status = 401;
	    		this.responseBody.put("mess", "Username and password do not match up");
			} else {
			
	    		Client client = new Client()
	            		.withName(reqBody.getString("Name"))
	            		.withToken(UUID.randomUUID().toString())
	            		.withUserId(user.get().getId());
	    		
	    		Client newClient = this.jdbi.withExtension(ClientDao.class, dao -> 
	    			dao.insertClient(client)
	    		);
	
	    		JSONObject clientJson = new JSONObject(newClient);
	    		this.responseBody.put("client", clientJson);
	    		this.status = 201;
	    		this.headers.put("Location", this.path + "/" + newClient.getId());
			}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A name and user id are required to create a client");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	private Permission mapToNull(Permission perm) {
		if (perm == null) {
			return null;
		}
		
		if (perm.getAddress() < 1) {
			perm.setAddress(null);
		}
		
		if (perm.getEmail() < 1) {
			perm.setEmail(null);
		}
		
		if (perm.getPhoneNumber() < 1) {
			perm.setPhoneNumber(null);
		}
		
		if (perm.getProfile() < 1) {
			perm.setProfile(null);
		}
		
		return perm;
	}
	
	private GatewayResponse handlePostToken(JSONObject reqBody, JSONObject pathParams) {
		try {
			String[] token = this.headers.get("Authorization").trim().split(" ");
			token = Arrays.stream(token).filter(str -> !str.isEmpty()).toArray(String[]::new);
			
			Optional<Client> client = this.AuthenticateClient(token);
			Optional<User> user = this.AuthenticateUser(reqBody.getString("username"), reqBody.getString("password"));
			
			if (!user.isPresent() || !client.isPresent()) {
				System.out.println("User not authenticated");
	    		this.status = 401;
	    		this.responseBody.put("mess", "Username and password do not match up");
			} else {
				JSONObject perms = reqBody.getJSONObject("permissions");
				
				Algorithm algorithm = Algorithm.HMAC256("aljd7ai3bq3ba$*vabw8#**&ASHJ*");
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(new Date());
			    cal.add(Calendar.HOUR_OF_DAY, 1);
			    
			    String accessToken = JWT.create()
			        .withIssuer("OneInfo")
			        .withExpiresAt(cal.getTime())
			        .withClaim("address", perms.optLong("address", -1))
			        .withClaim("profile", perms.optLong("profile", -1))
			        .withClaim("email", perms.optLong("email", -1))
			        .withClaim("phone_number", perms.optLong("phone_number", -1))
			        .sign(algorithm);
				
			   
	    		Permission defaultNewPermission = new Permission()
	    				.withUserId(user.get().getId())
	    				.withClient(client.get().getId())
	    				.withProfile(perms.optLong("profile", -1))
	    				.withEmail(perms.optLong("email", -1))
	    				.withPhoneNumber(perms.optLong("phone_number", -1))
	    				.withAddress(perms.optLong("address", -1))
	    				.withAccessToken(accessToken);
				
	    		Permission permission = this.mapToNull(defaultNewPermission);
				
	    		Permission newPermision = this.jdbi.withExtension(PermissionDao.class, dao -> 
	    			dao.insertPermission(permission)
	    		);
	
	    		this.responseBody.put("token", accessToken);
	    		this.status = 201;
			}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    		this.status = 400;
    		this.responseBody.put("mess", "A username, password and permissions for at least one of email, profile, phone number and address are needed");
    	}
		
		return new GatewayResponse(this.responseBody.toString(), this.headers, this.status);
	}
	
	public static void main(String[] args) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256("aljd7ai3bq3ba$*vabw8#**&ASHJ*");
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date());
		    cal.add(Calendar.HOUR_OF_DAY, 1);
		    String token = JWT.create()
		        .withIssuer("OneInfo")
		        .withExpiresAt(cal.getTime())
		        .withClaim("prm", "address:1")
		        .sign(algorithm);
		    
		    
		    JWTVerifier verifier = JWT.require(algorithm)
		            .withIssuer("OneInfo")
		            .build(); //Reusable verifier instance
		        DecodedJWT jwt = verifier.verify(token);
		        
		    System.out.println(jwt.getClaim("prm").asString());
		} catch (UnsupportedEncodingException exception){
		    //UTF-8 encoding not supported
			System.out.println("not encoded");
		} catch (JWTCreationException exception){
		    //Invalid Signing configuration / Couldn't convert Claims.
			System.out.println("not created");
		} catch (JWTVerificationException exception){
		    //Invalid signature/claims
			System.out.println("not valid");
		}
		
		System.out.println(UUID.randomUUID().toString());
	}
}