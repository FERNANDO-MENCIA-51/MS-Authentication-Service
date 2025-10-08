package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.dto.AssignRoleRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.RolePermissionAssignmentDto;
import edu.pe.vallegrande.AuthenticationService.dto.UserRoleAssignmentDto;
import edu.pe.vallegrande.AuthenticationService.exception.DuplicateResourceException;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.model.Permission;
import edu.pe.vallegrande.AuthenticationService.model.RolePermission;
import edu.pe.vallegrande.AuthenticationService.model.UserRole;
import edu.pe.vallegrande.AuthenticationService.repository.PermissionRepository;
import edu.pe.vallegrande.AuthenticationService.repository.RolePermissionRepository;
import edu.pe.vallegrande.AuthenticationService.repository.RoleRepository;
import edu.pe.vallegrande.AuthenticationService.repository.UserRepository;
import edu.pe.vallegrande.AuthenticationService.repository.UserRoleRepository;
import edu.pe.vallegrande.AuthenticationService.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación del servicio de asignaciones
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // === GESTIÓN USUARIO-ROL ===

    @Override
    public Flux<UserRoleAssignmentDto> getUserRoles(UUID userId) {
        log.info("Obteniendo roles del usuario: {}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado con ID: " + userId)))
                .flatMapMany(user -> userRoleRepository.findByUserId(userId)
                        .flatMap(this::mapUserRoleToDto));
    }

    @Override
    public Mono<UserRoleAssignmentDto> assignRoleToUser(UUID userId, UUID roleId, AssignRoleRequestDto request) {
        log.info("Asignando rol {} al usuario {}", roleId, userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado con ID: " + userId)))
                .then(roleRepository.findById(roleId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + roleId)))
                .then(userRoleRepository.existsByUserIdAndRoleId(userId, roleId))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException("El usuario ya tiene asignado este rol"));
                    }

                    UserRole userRole = UserRole.builder()
                            .userId(userId)
                            .roleId(roleId)
                            .assignedBy(request.getAssignedBy())
                            .assignedAt(LocalDateTime.now())
                            .expirationDate(request.getExpirationDate())
                            .active(request.getActive() != null ? request.getActive() : true)
                            .build();

                    return userRoleRepository.save(userRole);
                })
                .flatMap(this::mapUserRoleToDto)
                .doOnSuccess(assignment -> log.info("Rol asignado exitosamente: {} -> {}", userId, roleId));
    }

    @Override
    public Mono<Void> removeRoleFromUser(UUID userId, UUID roleId) {
        log.info("Quitando rol {} del usuario {}", roleId, userId);

        return userRoleRepository.existsByUserIdAndRoleId(userId, roleId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Asignación no encontrada"));
                    }
                    return userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
                })
                .doOnSuccess(unused -> log.info("Rol removido exitosamente: {} -> {}", userId, roleId));
    }

    @Override
    public Flux<UserRoleAssignmentDto> getUsersWithRole(UUID roleId) {
        log.info("Obteniendo usuarios con el rol: {}", roleId);

        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + roleId)))
                .flatMapMany(role -> userRoleRepository.findByRoleId(roleId)
                        .flatMap(this::mapUserRoleToDto));
    }

    // === GESTIÓN ROL-PERMISO ===

    @Override
    public Flux<RolePermissionAssignmentDto> getRolePermissions(UUID roleId) {
        log.info("Obteniendo permisos del rol: {}", roleId);

        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + roleId)))
                .flatMapMany(role -> rolePermissionRepository.findByRoleId(roleId)
                        .map(this::mapRolePermissionToDto));
    }

    @Override
    public Mono<RolePermissionAssignmentDto> assignPermissionToRole(UUID roleId, UUID permissionId) {
        log.info("Asignando permiso {} al rol {}", permissionId, roleId);

        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Rol no encontrado con ID: " + roleId)))
                .then(rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException("El rol ya tiene asignado este permiso"));
                    }

                    RolePermission rolePermission = RolePermission.builder()
                            .roleId(roleId)
                            .permissionId(permissionId)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return rolePermissionRepository.save(rolePermission);
                })
                .map(this::mapRolePermissionToDto)
                .doOnSuccess(assignment -> log.info("Permiso asignado exitosamente: {} -> {}", roleId, permissionId));
    }

    @Override
    public Mono<Void> removePermissionFromRole(UUID roleId, UUID permissionId) {
        log.info("Quitando permiso {} del rol {}", permissionId, roleId);

        return rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ResourceNotFoundException("Asignación no encontrada"));
                    }
                    return rolePermissionRepository.deleteByRoleIdAndPermissionId(roleId, permissionId);
                })
                .doOnSuccess(unused -> log.info("Permiso removido exitosamente: {} -> {}", roleId, permissionId));
    }

    @Override
    public Flux<RolePermissionAssignmentDto> getRolesWithPermission(UUID permissionId) {
        log.info("Obteniendo roles con el permiso: {}", permissionId);

        return rolePermissionRepository.findByPermissionId(permissionId)
                .map(this::mapRolePermissionToDto);
    }

    // === CONSULTAS AVANZADAS ===

    @Override
    public Flux<RolePermissionAssignmentDto> getUserEffectivePermissions(UUID userId) {
        log.info("Obteniendo permisos efectivos del usuario: {}", userId);

        return permissionRepository.findUserPermissions(userId)
                .map(this::mapPermissionToDto);
    }

    @Override
    public Mono<Boolean> userHasPermission(UUID userId, String module, String action, String resource) {
        log.info("Verificando si usuario {} tiene permiso: {}:{}:{}", userId, module, action, resource);

        return permissionRepository.findUserPermissions(userId)
                .any(permission -> module.equals(permission.getModule()) &&
                        action.equals(permission.getAction()) &&
                        (resource == null || resource.equals(permission.getResource())));
    }

    @Override
    public Mono<Boolean> userHasRole(UUID userId, UUID roleId) {
        log.info("Verificando si usuario {} tiene rol: {}", userId, roleId);

        return userRoleRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    // === MÉTODOS AUXILIARES ===

    private Mono<UserRoleAssignmentDto> mapUserRoleToDto(UserRole userRole) {
        return Mono.zip(
                userRepository.findById(userRole.getUserId()),
                roleRepository.findById(userRole.getRoleId()),
                userRole.getAssignedBy() != null ? userRepository.findById(userRole.getAssignedBy()) : Mono.empty())
                .map(tuple -> {
                    var user = tuple.getT1();
                    var role = tuple.getT2();
                    var assignedByUser = tuple.getT3();

                    return UserRoleAssignmentDto.builder()
                            .userId(userRole.getUserId())
                            .username(user.getUsername())
                            .roleId(userRole.getRoleId())
                            .roleName(role.getName())
                            .roleDescription(role.getDescription())
                            .assignedBy(userRole.getAssignedBy())
                            .assignedByUsername(assignedByUser != null ? assignedByUser.getUsername() : null)
                            .assignedAt(userRole.getAssignedAt())
                            .expirationDate(userRole.getExpirationDate())
                            .active(userRole.getActive())
                            .build();
                });
    }

    private RolePermissionAssignmentDto mapRolePermissionToDto(RolePermission rolePermission) {
        // Implementación temporal - en producción obtener datos completos de BD
        return RolePermissionAssignmentDto.builder()
                .roleId(rolePermission.getRoleId())
                .roleName("Role Name") // Temporal
                .permissionId(rolePermission.getPermissionId())
                .module("Module") // Temporal
                .action("Action") // Temporal
                .resource("Resource") // Temporal
                .description("Description") // Temporal
                .createdAt(rolePermission.getCreatedAt())
                .build();
    }

    private RolePermissionAssignmentDto mapPermissionToDto(Permission permission) {
        return RolePermissionAssignmentDto.builder()
                .permissionId(permission.getId())
                .module(permission.getModule())
                .action(permission.getAction())
                .resource(permission.getResource())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}