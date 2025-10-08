package edu.pe.vallegrande.AuthenticationService.service;

import edu.pe.vallegrande.AuthenticationService.dto.RoleRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.RoleResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz del servicio para la gestión de roles
 */
public interface RoleService {
    
    /**
     * Crear un nuevo rol
     */
    Mono<RoleResponseDto> createRole(RoleRequestDto roleRequestDto);
    
    /**
     * Obtener todos los roles
     */
    Flux<RoleResponseDto> getAllRoles();
    
    /**
     * Obtener roles activos
     */
    Flux<RoleResponseDto> getActiveRoles();
    
    /**
     * Obtener roles inactivos
     */
    Flux<RoleResponseDto> getInactiveRoles();
    
    /**
     * Obtener rol por ID
     */
    Mono<RoleResponseDto> getRoleById(UUID id);
    
    /**
     * Obtener rol por nombre
     */
    Mono<RoleResponseDto> getRoleByName(String name);
    
    /**
     * Actualizar un rol
     */
    Mono<RoleResponseDto> updateRole(UUID id, RoleRequestDto roleRequestDto);
    
    /**
     * Eliminar un rol (eliminación lógica)
     */
    Mono<Void> deleteRole(UUID id);
    
    /**
     * Restaurar un rol eliminado
     */
    Mono<RoleResponseDto> restoreRole(UUID id);
    
    /**
     * Verificar si existe un rol por nombre
     */
    Mono<Boolean> existsByName(String name);
}