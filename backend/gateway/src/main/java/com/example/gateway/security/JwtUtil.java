package com.example.gateway.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtil {

    private String secretKey;

    private Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") Long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String listenerId) {

        return Jwts.builder()
                .subject(listenerId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractListenerId(String token) {

        return extractClaim(token, Claims::getSubject);

    }

    public Boolean validateToken(String token, String listenerId) {
        final String extractId = extractListenerId(token);
        return (extractId.equals(listenerId) && !isTokenExpired(token));

    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateRefreshToken(String listenerId) {
        return generateToken(listenerId);
    }

    public Boolean validateRefreshToken(String token, String listenerId) {
        return validateToken(token, listenerId);
    }

    public String extractRefreshToken(String token) {
        return extractListenerId(token);
    }

}
