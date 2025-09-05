
package com.example.auth.api;

import com.example.auth.security.JwtService;
import com.example.auth.user.ObjectID;
import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import jakarta.validation.constraints.NotBlank;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo; this.encoder = encoder; this.jwt = jwt;
    }

    public static record LoginReq(@NotBlank String username, @NotBlank String password) {}
    public static record RegisterReq(Long id,@NotBlank String username, @NotBlank String password, @NotBlank String role) {}

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Executing logic on application startup using @EventListener! created user admin");
        User u = new User();
        u.setUsername("admin");
        u.setPassword(encoder.encode("123456"));
        u.setRoles(List.of("ROLE_ADMIN"));
        repo.saveAndFlush(u);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req){
        if (repo.findByUsername(req.username()).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error","username_taken"));
        User u = new User();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.setRoles(List.of(req.role()));
        repo.saveAndFlush(u);
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateUser")
    public ResponseEntity<?> update(@RequestBody RegisterReq req){
    	Optional<User> user = repo.findById(req.id());
    	if (user.isPresent()) {
	        User u=user.get();
	        u.setUsername(req.username());
	        u.setPassword(encoder.encode(req.password()));
	        u.setRoles(new ArrayList<>(Arrays.asList(req.role())));
	        repo.saveAndFlush(u);
    	}
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteUser")
    public ResponseEntity<?> delete(@RequestBody ObjectID id){
    	repo.deleteById(id.getId());
        return ResponseEntity.ok(Map.of("message","ok"));
    }
    
    @GetMapping("/listUser")
    public ResponseEntity<?> listUser(){
        List<User> u = repo.findAll();
        return ResponseEntity.ok(u);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req){
        var u = repo.findByUsername(req.username()).orElse(null);
        if (u == null || !encoder.matches(req.password(), u.getPassword()))
            return ResponseEntity.status(401).body(Map.of("error","bad_credentials"));
        String access = jwt.generateAccessToken(u.getUsername(), u.getRoles());
        String refresh = jwt.generateRefreshToken(u.getUsername());
        return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        try {
            String rt = body.get("refreshToken");
            var claims = jwt.parse(rt).getBody();
            if (!"refresh".equals(claims.get("type", String.class))) return ResponseEntity.status(401).build();
            String sub = claims.getSubject();
            var u = repo.findByUsername(sub).orElse(null);
            if (u == null) return ResponseEntity.status(401).build();
            String access = jwt.generateAccessToken(u.getUsername(), u.getRoles());
            String newRefresh = jwt.generateRefreshToken(u.getUsername());
            return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", newRefresh));
        } catch (Exception e) { return ResponseEntity.status(401).build(); }
    }
}
