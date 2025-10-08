package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.dto.LoginRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.LoginResponseDto;
import edu.pe.vallegrande.AuthenticationService.dto.RefreshTokenRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.TokenResponseDto;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.model.User;
import edu.pe.vallegrande.AuthenticationService.repository.UserRepository;
import edu.pe.vallegrande.AuthenticationService.service.AuthService;
import edu.pe.vallegrande.AuthenticationService.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementación del servicio de autenticación
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Set para almacenar tokens invalidados (en producción usar Redis)
    private final Set<String> blacklistedTokens = new HashSet<>();

    @Override
    public Mono<LoginResponseDto> login(LoginRequestDto loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getUsername());

        return userRepository.findByUsername(loginRequest.getUsername())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado")))
                .flatMap(user -> {
                    // Validar password (implementación temporal)
                    if (!validatePassword(loginRequest.getPassword(), user.getPasswordHash())) {
                        return incrementLoginAttempts(user)
                                .then(Mono.error(new RuntimeException("Credenciales inválidas")));
                    }

                    // Validar estado del usuario
                    if (!"ACTIVE".equals(user.getStatus())) {
                        return Mono.error(new RuntimeException("Usuario inactivo o suspendido"));
                    }

                    // Verificar si está bloqueado
                    if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(LocalDateTime.now())) {
                        return Mono.error(new RuntimeException("Usuario bloqueado hasta: " + user.getBlockedUntil()));
                    }

                    // Login exitoso
                    return processSuccessfulLogin(user);
                })
                .doOnSuccess(response -> log.info("Login exitoso para usuario: {}", loginRequest.getUsername()))
                .doOnError(error -> log.error("Error en login para usuario {}: {}", loginRequest.getUsername(),
                        error.getMessage()));
    }

    @Override
    public Mono<Void> logout(String token) {
        log.info("Cerrando sesión");

        // Agregar token a la blacklist
        blacklistedTokens.add(token);

        return Mono.<Void>empty()
                .doOnSuccess(unused -> log.info("Sesión cerrada exitosamente"));
    }

    @Override
    public Mono<TokenResponseDto> refreshToken(RefreshTokenRequestDto refreshRequest) {
        log.info("Renovando token");

        return jwtService.validateToken(refreshRequest.getRefreshToken())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new RuntimeException("Refresh token inválido"));
                    }

                    // Verificar que no esté en blacklist
                    if (blacklistedTokens.contains(refreshRequest.getRefreshToken())) {
                        return Mono.error(new RuntimeException("Token invalidado"));
                    }

                    return jwtService.extractUsername(refreshRequest.getRefreshToken());
                })
                .flatMap(username -> userRepository.findByUsername(username))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado")))
                .flatMap(user -> {
                    // Validar que el usuario siga activo
                    if (!"ACTIVE".equals(user.getStatus())) {
                        return Mono.error(new RuntimeException("Usuario inactivo"));
                    }

                    // Generar nuevos tokens
                    List<String> roles = Arrays.asList("USER"); // Temporal, luego obtener de BD
                    String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), roles);
                    String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

                    // Invalidar el refresh token anterior
                    blacklistedTokens.add(refreshRequest.getRefreshToken());

                    return Mono.just(TokenResponseDto.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .tokenType("Bearer")
                            .expiresIn(3600L) // 1 hora
                            .build());
                })
                .doOnSuccess(response -> log.info("Token renovado exitosamente"));
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        // Verificar blacklist
        if (blacklistedTokens.contains(token)) {
            return Mono.just(false);
        }

        return jwtService.validateToken(token);
    }

    /**
     * Procesar login exitoso
     */
    private Mono<LoginResponseDto> processSuccessfulLogin(User user) {
        // Actualizar último login y resetear intentos
        return userRepository.updateLastLogin(user.getId(), LocalDateTime.now())
                .then(Mono.fromCallable(() -> {
                    // Obtener roles del usuario (temporal)
                    List<String> roles = Arrays.asList("USER"); // Luego obtener de BD

                    // Generar tokens
                    String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), roles);
                    String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

                    return LoginResponseDto.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .tokenType("Bearer")
                            .expiresIn(3600L) // 1 hora
                            .userId(user.getId())
                            .username(user.getUsername())
                            .status(user.getStatus())
                            .roles(roles)
                            .loginTime(LocalDateTime.now())
                            .build();
                }));
    }

    /**
     * Incrementar intentos de login fallidos
     */
    private Mono<Void> incrementLoginAttempts(User user) {
        return userRepository.incrementLoginAttempts(user.getId())
                .then(Mono.fromRunnable(() -> {
                    // Si supera 5 intentos, bloquear por 30 minutos
                    if (user.getLoginAttempts() != null && user.getLoginAttempts() >= 4) {
                        LocalDateTime blockedUntil = LocalDateTime.now().plusMinutes(30);
                        userRepository.blockUser(user.getId(), blockedUntil).subscribe();
                        log.warn("Usuario {} bloqueado por múltiples intentos fallidos", user.getUsername());
                    }
                }));
    }

    /**
     * Validar password (implementación temporal)
     */
    private boolean validatePassword(String rawPassword, String hashedPassword) {
        // Implementación temporal - en producción usar PasswordEncoder
        return ("hashed_" + rawPassword).equals(hashedPassword);
    }
}