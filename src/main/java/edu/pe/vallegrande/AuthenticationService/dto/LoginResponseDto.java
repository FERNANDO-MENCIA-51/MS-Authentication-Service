package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuestas de login exitoso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // segundos
    private UUID userId;
    private String username;
    private String status;
    private List<String> roles;
    private LocalDateTime loginTime;
}