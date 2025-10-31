package com.library.librarymanagement.security;

import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtService {
    @Value("${secret.key}")
    private String secretKey;

    public String generateAccessToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().toString();
        Long accountId = -1L;
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            accountId = customUserDetails.getAccountId();
        }
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .claim("accountId", accountId)
                .setIssuer("nms")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) //15 minites
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateBorrowToken(String cardNumber, Long bookId, Timestamp borrowDate) {
        return Jwts.builder()
                .setSubject(cardNumber)
                .claim("bookId", bookId)
                .claim("borrowDate", borrowDate)
                .setIssuer("borrowToken")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException ex){
            return ex.getClaims().getSubject();
        }
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean isExpire = Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().getExpiration().before(new Date());

        return (!isExpire && username.equals(userDetails.getUsername()));
    }
}
