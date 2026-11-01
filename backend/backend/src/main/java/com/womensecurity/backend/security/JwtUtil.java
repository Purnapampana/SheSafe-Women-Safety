package com.womensecurity.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Minimum 32 characters for HS256
    private static final String SECRET =
            "WomenSecurityApplicationJwtSecretKey2026";

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24; // 24 hours

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // Generate JWT
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Extract Username (Email)
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();

    }

    // Extract Expiration
    public Date extractExpiration(String token) {

        return extractClaims(token).getExpiration();

    }

    // Validate Token
    public boolean validateToken(
            String token,
            String email) {

        String username =
                extractUsername(token);

        return username.equals(email)
                && !extractExpiration(token)
                        .before(new Date());

    }

    // Extract Claims
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

}