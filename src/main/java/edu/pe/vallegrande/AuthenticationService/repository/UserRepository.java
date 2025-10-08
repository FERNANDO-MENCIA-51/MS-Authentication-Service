package edu.pe.vallegrande.AuthenticationService.repository;

import edu.pe.vallegrande.AuthenticationService.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repositorio reactivo para la gestión de usuarios
 */
@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {
    
    /**
     * Buscar usuario por username
     */
    Mono<User> findByUsername(String username);
    
    /**
     * Buscar usuarios por status
     */
    Flux<User> findByStatus(String status);
    
    /**
     * Buscar usuarios activos
     */
    @Query("SELECT * FROM users WHERE status = 'ACTIVE'")
    Flux<User> findActiveUsers();
    
    /**
     * Buscar usuarios inactivos
     */
    @Query("SELECT * FROM users WHERE status = 'INACTIVE'")
    Flux<User> findInactiveUsers();
    
    /**
     * Buscar usuarios suspendidos
     */
    @Query("SELECT * FROM users WHERE status = 'SUSPENDED'")
    Flux<User> findSuspendedUsers();
    
    /**
     * Buscar usuarios por área
     */
    Flux<User> findByAreaId(UUID areaId);
    
    /**
     * Buscar usuarios por posición
     */
    Flux<User> findByPositionId(UUID positionId);
    
    /**
     * Buscar usuarios por manager directo
     */
    Flux<User> findByDirectManagerId(UUID managerId);
    
    /**
     * Verificar si existe un usuario con el username dado
     */
    Mono<Boolean> existsByUsername(String username);
    
    /**
     * Verificar si existe un usuario con el username dado excluyendo un ID específico
     */
    @Query("SELECT COUNT(*) > 0 FROM users WHERE username = :username AND id != :id")
    Mono<Boolean> existsByUsernameAndIdNot(String username, UUID id);
    
    /**
     * Actualizar el status de un usuario
     */
    @Query("UPDATE users SET status = :status, updated_at = NOW(), updated_by = :updatedBy WHERE id = :id")
    Mono<Integer> updateStatus(UUID id, String status, UUID updatedBy);
    
    /**
     * Actualizar último login
     */
    @Query("UPDATE users SET last_login = :lastLogin, login_attempts = 0 WHERE id = :id")
    Mono<Integer> updateLastLogin(UUID id, LocalDateTime lastLogin);
    
    /**
     * Incrementar intentos de login
     */
    @Query("UPDATE users SET login_attempts = login_attempts + 1 WHERE id = :id")
    Mono<Integer> incrementLoginAttempts(UUID id);
    
    /**
     * Bloquear usuario hasta una fecha específica
     */
    @Query("UPDATE users SET blocked_until = :blockedUntil, status = 'SUSPENDED' WHERE id = :id")
    Mono<Integer> blockUser(UUID id, LocalDateTime blockedUntil);
    
    /**
     * Desbloquear usuario
     */
    @Query("UPDATE users SET blocked_until = NULL, status = 'ACTIVE', login_attempts = 0 WHERE id = :id")
    Mono<Integer> unblockUser(UUID id);
}