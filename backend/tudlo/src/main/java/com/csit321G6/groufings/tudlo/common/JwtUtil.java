package com.csit321G6.groufings.tudlo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:}")
    private String secret;

    @Value("${app.jwt.expiration-minutes:480}")
    private long expiryMinutes;

    private SecretKey key;

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()
                || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secret must contain at least 32 bytes.");
        }
        if (expiryMinutes <= 0) {
            throw new IllegalStateException("app.jwt.expiration-minutes must be greater than zero");
        }
        try {
            getExpirySeconds();
        } catch (ArithmeticException exception) {
            throw new IllegalStateException("app.jwt.expiration-minutes is too large", exception);
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, String userType) {
        return generateToken(userId, email, userType, null);
    }

    public String generateToken(Long userId, String email, String role, Long profileId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(role, "role");
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(getExpirySeconds());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("profileId", profileId)
                .claim("expiryMinutes", expiryMinutes)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public long getExpirySeconds() {
        return Math.multiplyExact(expiryMinutes, 60L);
    }

    public long getExpiryMinutes() {
        return expiryMinutes;
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}