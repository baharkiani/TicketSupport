package com.example.TicketSupport.service;

import com.example.TicketSupport.entity.RefreshToken;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey generateKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(String id, User user) {

        String token = Jwts.builder()
                .subject(id)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + 7L * 24 * 60 * 60 * 1000
                ))
                .signWith(generateKey())
                .compact();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(hashToken(token));
        refreshToken.setUser(user);
        refreshToken.setIssuedAt(LocalDateTime.now());
        refreshToken.setExpirationAt(
                LocalDateTime.now().plusDays(7)
        );

        System.out.println(hashToken(token));
        refreshTokenRepository.save(refreshToken);

        return token;
    }


    public boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.getExpirationAt().isBefore(LocalDateTime.now());
    }


    public boolean isValidToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return "refresh".equals(claims.get("type", String.class))
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}