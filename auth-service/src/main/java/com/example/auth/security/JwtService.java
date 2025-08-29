
package com.example.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;
    private final long accessTtl;
    private final long refreshTtl;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.issuer:auth-service}") String issuer,
                      @Value("${jwt.access-token-ttl:900}") long accessTtl,
                      @Value("${jwt.refresh-token-ttl:1209600}") long refreshTtl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(String sub, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(sub)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtl)))
                .addClaims(Map.of("roles", roles))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String sub) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(sub)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtl)))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
    }
}
