
package com.example.userapi.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.model.DataTableObject;
import com.example.model.ObjectID;
import com.example.model.User;
import com.example.utils.UrlServer;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.Set;

@RestController
public class UserController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	public static record RegisterReq(Long id,@NotBlank String username, @NotBlank String password, @NotBlank String role) {}
	
    @GetMapping("/public/ping")
    public Map<String,String> ping(){ return Map.of("status","ok"); }

    @GetMapping("/public/listUser")
    public ResponseEntity<?> listUser(){
    	ResponseEntity<User[]> u = restTemplate.getForEntity(UrlServer.url_auth_services+"/auth/listUser", User[].class);
    	DataTableObject data = new DataTableObject();
    	data.setData(u.getBody());
    	return ResponseEntity.ok(data);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req){
    	restTemplate.postForObject(UrlServer.url_auth_services+"/auth/register", req, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/updateUser")
    public ResponseEntity<?> update(@RequestBody RegisterReq req){
    	restTemplate.postForObject(UrlServer.url_auth_services+"/auth/updateUser", req, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/deleteUser")
    public ResponseEntity<?> delete(@RequestBody ObjectID id){
    	restTemplate.postForObject(UrlServer.url_auth_services+"/auth/deleteUser", id, String.class);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
}
