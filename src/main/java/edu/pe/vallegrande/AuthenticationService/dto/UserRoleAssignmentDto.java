package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para asignaciones de roles a usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentDto {
    
    private UUID userId;
    private String username;
    private UUID roleId;
    private String roleName;
    private String roleDescription;
    private UUID assignedBy;
    private String assignedByUsername;
    private LocalDateTime assignedAt;
    private LocalDate expirationDate;
    private Boolean active;
}