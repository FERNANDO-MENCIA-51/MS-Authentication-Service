package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO para las solicitudes de creación y actualización de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    
    private String username;
    private String password; // Solo para creación, se hashea internamente
    private UUID personId;
    private UUID areaId;
    private UUID positionId;
    private UUID directManagerId;
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private Map<String, Object> preferences;
    private UUID createdBy;
    private UUID updatedBy;
}