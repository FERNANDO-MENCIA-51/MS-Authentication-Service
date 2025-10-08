package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.dto.RoleRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.RoleResponseDto;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.exception.DuplicateResourceException;
import edu.pe.vallegrande.AuthenticationService.model.Role;
import edu.pe.vallegrande.AuthenticationService.repository.RoleRepository;
import edu.pe.vallegrande.AuthenticationService.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación del servicio para la gestión de roles
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    
    @Override
    public Mono<RoleResponseDto> createRole(RoleRequestDto roleRequestDto) {
        log.info("Creando nuevo rol: {}", roleRequestDto.getName());
        
        return roleRepository.existsByName(roleRequestDto.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException("Ya existe un rol con el nombre: " + roleRequestDto.getName()));
                    }
                    
                    Role role = Role.builder()
                            .id(UUID.randomUUID())
                            .name(roleRequestDto.getName())
                            .description(roleRequestDto.getDescription())
                            .isSystem(roleRequestDto.getIsSystem() != null ? roleRequestDto.getIsSystem() : false)
                            .active(roleRequestDto.getActive() != null ? roleRequestDto.getActive() : true)
                            .createdAt(LocalDateTime.now())
                            .createdBy(roleRequestDto.getCreatedBy())
                            .build();
                    
                    return roleRepository.save(role);
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(role -> log.info("Rol creado exitosamente: {}", role.getName()))
                .doOnError(error -> log.error("Error al crear rol: {}", error.getMessage()));
    }
    
    @Override
    public Flux<RoleResponseDto> getAllRoles() {
        log.info("Obteniendo todos los roles");
        return roleRepository.findAll()
                .map(this::mapToResponseDto)
                .doOnComplete(() -> log.info("Roles obtenidos exitosamente"));
    }
    
    @Override
    public Flux<RoleResponseDto> getActiveRoles() {
        log.info("Obteniendo roles activos");
        return roleRepository.findByActiveTrue()
                .map(this::mapToResponseDto)
                .doOnComplete(() -> log.info("Roles activos obtenidos exitosamente"));
    }
    
    @Override
    public Flux<RoleResponseDto> getInactiveRoles() {
        log.info("Obteniendo roles inactivos");
        return roleRepository.findByActiveFalse()
                .map(this::mapToResponseDto)
                .doOnComplete(() -> log.info("Roles inactivos obtenidos exitosamente"));
    }
    
    @Override
    public Mono<RoleResponseDto> getRoleById(UUID id) {
        log.info("Obteniendo rol por ID: {}", id);
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + id)))
                .map(this::mapToResponseDto)
                .doOnSuccess(role -> log.info("Rol encontrado: {}", role.getName()));
    }
    
    @Override
    public Mono<RoleResponseDto> getRoleByName(String name) {
        log.info("Obteniendo rol por nombre: {}", name);
        return roleRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con nombre: " + name)))
                .map(this::mapToResponseDto)
                .doOnSuccess(role -> log.info("Rol encontrado: {}", role.getName()));
    }
    
    @Override
    public Mono<RoleResponseDto> updateRole(UUID id, RoleRequestDto roleRequestDto) {
        log.info("Actualizando rol con ID: {}", id);
        
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + id)))
                .flatMap(existingRole -> {
                    // Verificar si el nombre ya existe en otro rol
                    if (!existingRole.getName().equals(roleRequestDto.getName())) {
                        return roleRepository.existsByNameAndIdNot(roleRequestDto.getName(), id)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new DuplicateResourceException("Ya existe un rol con el nombre: " + roleRequestDto.getName()));
                                    }
                                    return Mono.just(existingRole);
                                });
                    }
                    return Mono.just(existingRole);
                })
                .flatMap(existingRole -> {
                    Role updatedRole = Role.builder()
                            .id(existingRole.getId())
                            .name(roleRequestDto.getName())
                            .description(roleRequestDto.getDescription())
                            .isSystem(roleRequestDto.getIsSystem() != null ? roleRequestDto.getIsSystem() : existingRole.getIsSystem())
                            .active(roleRequestDto.getActive() != null ? roleRequestDto.getActive() : existingRole.getActive())
                            .createdAt(existingRole.getCreatedAt())
                            .createdBy(existingRole.getCreatedBy())
                            .build();
                    
                    return roleRepository.save(updatedRole);
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(role -> log.info("Rol actualizado exitosamente: {}", role.getName()))
                .doOnError(error -> log.error("Error al actualizar rol: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Void> deleteRole(UUID id) {
        log.info("Eliminando rol con ID: {}", id);
        
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + id)))
                .flatMap(role -> {
                    if (role.getIsSystem()) {
                        return Mono.error(new IllegalStateException("No se puede eliminar un rol del sistema"));
                    }
                    return roleRepository.updateActiveStatus(id, false);
                })
                .then()
                .doOnSuccess(unused -> log.info("Rol eliminado exitosamente con ID: {}", id))
                .doOnError(error -> log.error("Error al eliminar rol: {}", error.getMessage()));
    }
    
    @Override
    public Mono<RoleResponseDto> restoreRole(UUID id) {
        log.info("Restaurando rol con ID: {}", id);
        
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + id)))
                .flatMap(role -> {
                    if (role.getActive()) {
                        return Mono.error(new IllegalStateException("El rol ya está activo"));
                    }
                    return roleRepository.updateActiveStatus(id, true)
                            .then(Mono.just(role));
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(role -> log.info("Rol restaurado exitosamente: {}", role.getName()))
                .doOnError(error -> log.error("Error al restaurar rol: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByName(String name) {
        return roleRepository.existsByName(name);
    }
    
    /**
     * Mapea una entidad Role a RoleResponseDto
     */
    private RoleResponseDto mapToResponseDto(Role role) {
        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .active(role.getActive())
                .createdAt(role.getCreatedAt())
                .createdBy(role.getCreatedBy())
                .build();
    }
}