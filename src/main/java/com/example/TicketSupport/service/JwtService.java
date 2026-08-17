package com.example.TicketSupport.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey generateKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwt(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(generateKey())
                .compact();
    }

    public String extractUsername(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateJwt(String token) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}