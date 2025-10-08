package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para las respuestas de usuarios
 * No incluye información sensible como password_hash
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    private UUID id;
    private String username;
    private UUID personId;
    private UUID areaId;
    private UUID positionId;
    private UUID directManagerId;
    private String status;
    private LocalDateTime lastLogin;
    private Integer loginAttempts;
    private LocalDateTime blockedUntil;
    private Map<String, Object> preferences;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private UUID updatedBy;
    private LocalDateTime updatedAt;
    private Integer version;
}