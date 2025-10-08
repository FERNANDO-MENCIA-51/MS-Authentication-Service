package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio JWT
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    
    private final SecretKey secretKey;
    private final long accessTokenExpiration = 3600; // 1 hora en segundos
    private final long refreshTokenExpiration = 604800; // 7 días en segundos
    
    public JwtServiceImpl() {
        // Generar clave secreta segura usando el nuevo método
        this.secretKey = Jwts.SIG.HS512.key().build();
    }
    
    @Override
    public String generateAccessToken(UUID userId, String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);
        
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    @Override
    public String generateRefreshToken(UUID userId, String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);
        
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error al extraer claims: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    @Override
    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        if (claims != null) {
            String userIdStr = claims.get("userId", String.class);
            return userIdStr != null ? UUID.fromString(userIdStr) : null;
        }
        return null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? (List<String>) claims.get("roles") : List.of();
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims != null && claims.getExpiration().before(new Date());
    }
}
