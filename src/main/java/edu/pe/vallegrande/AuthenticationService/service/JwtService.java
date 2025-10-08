package edu.pe.vallegrande.AuthenticationService.service;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para manejo de tokens JWT
 */
public interface JwtService {
    
    /**
     * Generar token de acceso
     */
    String generateAccessToken(UUID userId, String username, List<String> roles);
    
    /**
     * Generar token de refresh
     */
    String generateRefreshToken(UUID userId, String username);
    
    /**
     * Validar token
     */
    Mono<Boolean> validateToken(String token);
    
    /**
     * Extraer claims del token
     */
    Mono<Claims> extractClaims(String token);
    
    /**
     * Extraer username del token
     */
    Mono<String> extractUsername(String token);
    
    /**
     * Extraer user ID del token
     */
    Mono<UUID> extractUserId(String token);
    
    /**
     * Extraer roles del token
     */
    Mono<List<String>> extractRoles(String token);
    
    /**
     * Verificar si el token ha expirado
     */
    Mono<Boolean> isTokenExpired(String token);
}