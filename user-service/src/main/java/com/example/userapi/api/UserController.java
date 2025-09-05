
package com.example.userapi.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.DataTableObject;
import com.example.model.ObjectID;
import com.example.model.User;
import com.example.utils.RestTemplateWithJwt;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@RestController
public class UserController {
	
	@Value("${server.url}")
	private String url_auth_services;
	
	@Autowired
	public RestTemplate restTemplate;
	
	public static record RegisterReq(Long id,@NotBlank String username, @NotBlank String password, @NotBlank String role) {}
	
    @GetMapping("/public/ping")
    public Map<String,String> ping(){ return Map.of("status","ok"); }

    @GetMapping("/public/listUser")
    public ResponseEntity<?> listUser(){
    	ResponseEntity<User[]> u = restTemplate.getForEntity(url_auth_services+":8081/auth/listUser", User[].class);
    	DataTableObject data = new DataTableObject();
    	data.setData(u.getBody());
    	return ResponseEntity.ok(data);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestHeader("Authorization") String authHeader,@RequestBody RegisterReq req){
    	String jwtToken = authHeader.replace("Bearer ", "");
    	RestTemplate restTemplate = RestTemplateWithJwt.restTemplate(jwtToken);
    	restTemplate.postForObject(url_auth_services+":8081/auth/register", req, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/updateUser")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,@RequestBody RegisterReq req){
    	String jwtToken = authHeader.replace("Bearer ", "");
    	RestTemplate restTemplate = RestTemplateWithJwt.restTemplate(jwtToken);
    	restTemplate.postForObject(url_auth_services+":8081/auth/updateUser", req, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/deleteUser")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader,@RequestBody ObjectID id){
    	String jwtToken = authHeader.replace("Bearer ", "");
    	RestTemplate restTemplate = RestTemplateWithJwt.restTemplate(jwtToken);
    	restTemplate.postForObject(url_auth_services+":8081/auth/deleteUser", id, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
}
