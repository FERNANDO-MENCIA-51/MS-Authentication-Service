package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para las solicitudes de creación y actualización de roles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {
    
    private String name;
    private String description;
    private Boolean isSystem;
    private Boolean active;
    private UUID createdBy;
}