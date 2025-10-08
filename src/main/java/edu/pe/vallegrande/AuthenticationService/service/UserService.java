package edu.pe.vallegrande.AuthenticationService.service;

import edu.pe.vallegrande.AuthenticationService.dto.UserRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.UserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz del servicio para la gestión de usuarios
 */
public interface UserService {
    
    /**
     * Crear un nuevo usuario
     */
    Mono<UserResponseDto> createUser(UserRequestDto userRequestDto);
    
    /**
     * Obtener todos los usuarios
     */
    Flux<UserResponseDto> getAllUsers();
    
    /**
     * Obtener usuarios activos
     */
    Flux<UserResponseDto> getActiveUsers();
    
    /**
     * Obtener usuarios inactivos
     */
    Flux<UserResponseDto> getInactiveUsers();
    
    /**
     * Obtener usuarios suspendidos
     */
    Flux<UserResponseDto> getSuspendedUsers();
    
    /**
     * Obtener usuario por ID
     */
    Mono<UserResponseDto> getUserById(UUID id);
    
    /**
     * Obtener usuario por username
     */
    Mono<UserResponseDto> getUserByUsername(String username);
    
    /**
     * Obtener usuarios por área
     */
    Flux<UserResponseDto> getUsersByArea(UUID areaId);
    
    /**
     * Obtener usuarios por posición
     */
    Flux<UserResponseDto> getUsersByPosition(UUID positionId);
    
    /**
     * Obtener usuarios por manager
     */
    Flux<UserResponseDto> getUsersByManager(UUID managerId);
    
    /**
     * Actualizar un usuario
     */
    Mono<UserResponseDto> updateUser(UUID id, UserRequestDto userRequestDto);
    
    /**
     * Cambiar status de usuario
     */
    Mono<UserResponseDto> changeUserStatus(UUID id, String status, UUID updatedBy);
    
    /**
     * Eliminar un usuario (cambio de status a INACTIVE)
     */
    Mono<Void> deleteUser(UUID id, UUID updatedBy);
    
    /**
     * Restaurar un usuario eliminado
     */
    Mono<UserResponseDto> restoreUser(UUID id, UUID updatedBy);
    
    /**
     * Suspender usuario
     */
    Mono<UserResponseDto> suspendUser(UUID id, UUID updatedBy);
    
    /**
     * Bloquear usuario temporalmente
     */
    Mono<UserResponseDto> blockUser(UUID id);
    
    /**
     * Desbloquear usuario
     */
    Mono<UserResponseDto> unblockUser(UUID id);
    
    /**
     * Verificar si existe un usuario por username
     */
    Mono<Boolean> existsByUsername(String username);
    
    /**
     * Actualizar último login
     */
    Mono<Void> updateLastLogin(UUID id);
    
    /**
     * Incrementar intentos de login fallidos
     */
    Mono<Void> incrementLoginAttempts(UUID id);
}