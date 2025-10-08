package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.AssignRoleRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.RolePermissionAssignmentDto;
import edu.pe.vallegrande.AuthenticationService.dto.UserRoleAssignmentDto;
import edu.pe.vallegrande.AuthenticationService.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Controlador REST para la gestión de asignaciones usuario-rol y rol-permiso
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Assignments", description = "API para la gestión de asignaciones de roles y permisos")
public class AssignmentController {
    
    private final AssignmentService assignmentService;
    
    // === GESTIÓN USUARIO-ROL ===
    
    @Operation(summary = "Obtener roles asignados a un usuario")
    @GetMapping("/users/{userId}/roles")
    public Flux<UserRoleAssignmentDto> getUserRoles(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.info("Solicitud para obtener roles del usuario: {}", userId);
        return assignmentService.getUserRoles(userId);
    }
    
    @Operation(summary = "Asignar rol a usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol asignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado"),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene este rol asignado")
    })
    @PostMapping("/users/{userId}/roles/{roleId}")
    public Mono<ResponseEntity<UserRoleAssignmentDto>> assignRoleToUser(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "ID del rol") @PathVariable UUID roleId,
            @RequestBody AssignRoleRequestDto request) {
        log.info("Solicitud para asignar rol {} al usuario {}", roleId, userId);
        return assignmentService.assignRoleToUser(userId, roleId, request)
                .map(assignment -> ResponseEntity.status(HttpStatus.CREATED).body(assignment));
    }
    
    @Operation(summary = "Quitar rol de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol removido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public Mono<ResponseEntity<Void>> removeRoleFromUser(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "ID del rol") @PathVariable UUID roleId) {
        log.info("Solicitud para quitar rol {} del usuario {}", roleId, userId);
        return assignmentService.removeRoleFromUser(userId, roleId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    @Operation(summary = "Obtener usuarios con un rol específico")
    @GetMapping("/roles/{roleId}/users")
    public Flux<UserRoleAssignmentDto> getUsersWithRole(
            @Parameter(description = "ID del rol") @PathVariable UUID roleId) {
        log.info("Solicitud para obtener usuarios con el rol: {}", roleId);
        return assignmentService.getUsersWithRole(roleId);
    }
    
    // === GESTIÓN ROL-PERMISO ===
    
    @Operation(summary = "Obtener permisos asignados a un rol")
    @GetMapping("/roles/{roleId}/permissions")
    public Flux<RolePermissionAssignmentDto> getRolePermissions(
            @Parameter(description = "ID del rol") @PathVariable UUID roleId) {
        log.info("Solicitud para obtener permisos del rol: {}", roleId);
        return assignmentService.getRolePermissions(roleId);
    }
    
    @Operation(summary = "Asignar permiso a rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permiso asignado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Rol o permiso no encontrado"),
            @ApiResponse(responseCode = "409", description = "El rol ya tiene este permiso asignado")
    })
    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    public Mono<ResponseEntity<RolePermissionAssignmentDto>> assignPermissionToRole(
            @Parameter(description = "ID del rol") @PathVariable UUID roleId,
            @Parameter(description = "ID del permiso") @PathVariable UUID permissionId) {
        log.info("Solicitud para asignar permiso {} al rol {}", permissionId, roleId);
        return assignmentService.assignPermissionToRole(roleId, permissionId)
                .map(assignment -> ResponseEntity.status(HttpStatus.CREATED).body(assignment));
    }
    
    @Operation(summary = "Quitar permiso de rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permiso removido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    public Mono<ResponseEntity<Void>> removePermissionFromRole(
            @Parameter(description = "ID del rol") @PathVariable UUID roleId,
            @Parameter(description = "ID del permiso") @PathVariable UUID permissionId) {
        log.info("Solicitud para quitar permiso {} del rol {}", permissionId, roleId);
        return assignmentService.removePermissionFromRole(roleId, permissionId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    @Operation(summary = "Obtener roles que tienen un permiso específico")
    @GetMapping("/permissions/{permissionId}/roles")
    public Flux<RolePermissionAssignmentDto> getRolesWithPermission(
            @Parameter(description = "ID del permiso") @PathVariable UUID permissionId) {
        log.info("Solicitud para obtener roles con el permiso: {}", permissionId);
        return assignmentService.getRolesWithPermission(permissionId);
    }
    
    // === CONSULTAS AVANZADAS ===
    
    @Operation(summary = "Obtener todos los permisos efectivos de un usuario")
    @GetMapping("/users/{userId}/effective-permissions")
    public Flux<RolePermissionAssignmentDto> getUserEffectivePermissions(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.info("Solicitud para obtener permisos efectivos del usuario: {}", userId);
        return assignmentService.getUserEffectivePermissions(userId);
    }
    
    @Operation(summary = "Verificar si un usuario tiene un permiso específico")
    @GetMapping("/users/{userId}/has-permission")
    public Mono<ResponseEntity<Boolean>> userHasPermission(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Módulo del permiso") @RequestParam String module,
            @Parameter(description = "Acción del permiso") @RequestParam String action,
            @Parameter(description = "Recurso del permiso") @RequestParam(required = false) String resource) {
        log.info("Verificando si usuario {} tiene permiso: {}:{}:{}", userId, module, action, resource);
        return assignmentService.userHasPermission(userId, module, action, resource)
                .map(hasPermission -> ResponseEntity.ok(hasPermission));
    }
    
    @Operation(summary = "Verificar si un usuario tiene un rol específico")
    @GetMapping("/users/{userId}/has-role/{roleId}")
    public Mono<ResponseEntity<Boolean>> userHasRole(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "ID del rol") @PathVariable UUID roleId) {
        log.info("Verificando si usuario {} tiene rol: {}", userId, roleId);
        return assignmentService.userHasRole(userId, roleId)
                .map(hasRole -> ResponseEntity.ok(hasRole));
    }
}