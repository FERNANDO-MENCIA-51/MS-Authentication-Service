package edu.pe.vallegrande.AuthenticationService.service;

import io.jsonwebtoken.Claims;
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
     * Validar token (síncrono para filtros)
     */
    boolean validateToken(String token);
    
    /**
     * Extraer claims del token (síncrono)
     */
    Claims extractClaims(String token);
    
    /**
     * Extraer username del token (síncrono)
     */
    String extractUsername(String token);
    
    /**
     * Extraer user ID del token (síncrono)
     */
    UUID extractUserId(String token);
    
    /**
     * Extraer roles del token (síncrono)
     */
    List<String> extractRoles(String token);
    
    /**
     * Verificar si el token ha expirado (síncrono)
     */
    boolean isTokenExpired(String token);
}