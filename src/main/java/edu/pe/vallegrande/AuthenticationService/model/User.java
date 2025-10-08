package edu.pe.vallegrande.AuthenticationService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entidad User para el sistema de autenticación
 * Representa los usuarios del sistema con información completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    
    @Id
    private UUID id;
    
    @Column("username")
    private String username;
    
    @Column("password_hash")
    private String passwordHash;
    
    @Column("person_id")
    private UUID personId;
    
    @Column("area_id")
    private UUID areaId;
    
    @Column("position_id")
    private UUID positionId;
    
    @Column("direct_manager_id")
    private UUID directManagerId;
    
    @Column("status")
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    
    @Column("last_login")
    private LocalDateTime lastLogin;
    
    @Column("login_attempts")
    private Integer loginAttempts;
    
    @Column("blocked_until")
    private LocalDateTime blockedUntil;
    
    @Column("preferences")
    private Map<String, Object> preferences;
    
    @Column("created_by")
    private UUID createdBy;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_by")
    private UUID updatedBy;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
    
    @Column("version")
    private Integer version;
}