package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
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
        });
    }
    
    @Override
    public Mono<Claims> extractClaims(String token) {
        return Mono.fromCallable(() -> {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }).onErrorReturn(null);
    }
    
    @Override
    public Mono<String> extractUsername(String token) {
        return extractClaims(token)
                .map(Claims::getSubject);
    }
    
    @Override
    public Mono<UUID> extractUserId(String token) {
        return extractClaims(token)
                .map(claims -> UUID.fromString(claims.get("userId", String.class)));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Mono<List<String>> extractRoles(String token) {
        return extractClaims(token)
                .map(claims -> (List<String>) claims.get("roles"));
    }
    
    @Override
    public Mono<Boolean> isTokenExpired(String token) {
        return extractClaims(token)
                .map(claims -> claims.getExpiration().before(new Date()));
    }
}