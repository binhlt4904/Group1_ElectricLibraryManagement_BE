package com.library.librarymanagement.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtService {
    @Value("${secret.key}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().toString();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuer("nms")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) //15 minites
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean isExpire = Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().getExpiration().before(new Date());

        return (!isExpire && username.equals(userDetails.getUsername()));
    }
}
