package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.LoginRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.LoginResponseDto;
import edu.pe.vallegrande.AuthenticationService.dto.RefreshTokenRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.TokenResponseDto;
import edu.pe.vallegrande.AuthenticationService.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para autenticación
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API para autenticación y manejo de tokens")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "423", description = "Usuario bloqueado")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Solicitud de login para usuario: {}", loginRequest.getUsername());

        return authService.login(loginRequest)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> {
                    log.error("Error en login: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(LoginResponseDto.builder()
                                    .build()));
                });
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout exitoso"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(
            @Parameter(description = "Token JWT en el header Authorization") @RequestHeader("Authorization") String authHeader) {

        log.info("Solicitud de logout");

        // Extraer token del header "Bearer token"
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        return authService.logout(token)
                .then(Mono.just(ResponseEntity.ok("Logout exitoso")))
                .onErrorResume(error -> {
                    log.error("Error en logout: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Error en logout"));
                });
    }

    @Operation(summary = "Renovar token", description = "Genera un nuevo access token usando el refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponseDto>> refreshToken(@RequestBody RefreshTokenRequestDto refreshRequest) {
        log.info("Solicitud de refresh token");

        return authService.refreshToken(refreshRequest)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(error -> {
                    log.error("Error en refresh token: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(TokenResponseDto.builder().build()));
                });
    }

    @Operation(summary = "Validar token", description = "Verifica si un token JWT es válido")
    @ApiResponse(responseCode = "200", description = "Validación completada")
    @PostMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(
            @Parameter(description = "Token JWT en el header Authorization") @RequestHeader("Authorization") String authHeader) {

        log.info("Solicitud de validación de token");

        // Extraer token del header "Bearer token"
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        return authService.validateToken(token)
                .map(isValid -> ResponseEntity.ok(isValid))
                .onErrorReturn(ResponseEntity.ok(false));
    }
}