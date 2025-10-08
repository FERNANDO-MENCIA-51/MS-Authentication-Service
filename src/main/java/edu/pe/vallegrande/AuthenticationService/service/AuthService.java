package edu.pe.vallegrande.AuthenticationService.service;

import edu.pe.vallegrande.AuthenticationService.dto.LoginRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.LoginResponseDto;
import edu.pe.vallegrande.AuthenticationService.dto.RefreshTokenRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.TokenResponseDto;
import reactor.core.publisher.Mono;

/**
 * Servicio de autenticación
 */
public interface AuthService {
    
    /**
     * Iniciar sesión
     */
    Mono<LoginResponseDto> login(LoginRequestDto loginRequest);
    
    /**
     * Cerrar sesión
     */
    Mono<Void> logout(String token);
    
    /**
     * Renovar token
     */
    Mono<TokenResponseDto> refreshToken(RefreshTokenRequestDto refreshRequest);
    
    /**
     * Validar token
     */
    Mono<Boolean> validateToken(String token);
}