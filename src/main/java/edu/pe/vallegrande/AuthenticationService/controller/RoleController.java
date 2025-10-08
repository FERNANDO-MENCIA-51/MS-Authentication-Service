package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.RoleRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.RoleResponseDto;
import edu.pe.vallegrande.AuthenticationService.service.RoleService;
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
 * Controlador REST para la gestión de roles
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para la gestión de roles del sistema")
public class RoleController {
    
    private final RoleService roleService;
    
    @Operation(summary = "Crear un nuevo rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe un rol con ese nombre")
    })
    @PostMapping
    public Mono<ResponseEntity<RoleResponseDto>> createRole(@RequestBody RoleRequestDto roleRequestDto) {
        log.info("Solicitud para crear rol: {}", roleRequestDto.getName());
        return roleService.createRole(roleRequestDto)
                .map(role -> ResponseEntity.status(HttpStatus.CREATED).body(role));
    }
    
    @Operation(summary = "Obtener todos los roles")
    @GetMapping
    public Flux<RoleResponseDto> getAllRoles() {
        log.info("Solicitud para obtener todos los roles");
        return roleService.getAllRoles();
    }
    
    @Operation(summary = "Obtener roles activos")
    @GetMapping("/active")
    public Flux<RoleResponseDto> getActiveRoles() {
        log.info("Solicitud para obtener roles activos");
        return roleService.getActiveRoles();
    }
    
    @Operation(summary = "Obtener roles inactivos")
    @GetMapping("/inactive")
    public Flux<RoleResponseDto> getInactiveRoles() {
        log.info("Solicitud para obtener roles inactivos");
        return roleService.getInactiveRoles();
    }
    
    @Operation(summary = "Obtener rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RoleResponseDto>> getRoleById(
            @Parameter(description = "ID del rol") @PathVariable UUID id) {
        log.info("Solicitud para obtener rol por ID: {}", id);
        return roleService.getRoleById(id)
                .map(role -> ResponseEntity.ok(role));
    }
    
    @Operation(summary = "Obtener rol por nombre")
    @GetMapping("/name/{name}")
    public Mono<ResponseEntity<RoleResponseDto>> getRoleByName(
            @Parameter(description = "Nombre del rol") @PathVariable String name) {
        log.info("Solicitud para obtener rol por nombre: {}", name);
        return roleService.getRoleByName(name)
                .map(role -> ResponseEntity.ok(role));
    }
    
    @Operation(summary = "Actualizar un rol")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RoleResponseDto>> updateRole(
            @Parameter(description = "ID del rol") @PathVariable UUID id, 
            @RequestBody RoleRequestDto roleRequestDto) {
        log.info("Solicitud para actualizar rol con ID: {}", id);
        return roleService.updateRole(id, roleRequestDto)
                .map(role -> ResponseEntity.ok(role));
    }
    
    @Operation(summary = "Eliminar un rol (eliminación lógica)")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRole(
            @Parameter(description = "ID del rol") @PathVariable UUID id) {
        log.info("Solicitud para eliminar rol con ID: {}", id);
        return roleService.deleteRole(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    @Operation(summary = "Restaurar un rol eliminado")
    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<RoleResponseDto>> restoreRole(
            @Parameter(description = "ID del rol") @PathVariable UUID id) {
        log.info("Solicitud para restaurar rol con ID: {}", id);
        return roleService.restoreRole(id)
                .map(role -> ResponseEntity.ok(role));
    }
    
    @Operation(summary = "Verificar si existe un rol por nombre")
    @GetMapping("/exists/{name}")
    public Mono<ResponseEntity<Boolean>> existsByName(
            @Parameter(description = "Nombre del rol") @PathVariable String name) {
        log.info("Verificando existencia de rol con nombre: {}", name);
        return roleService.existsByName(name)
                .map(exists -> ResponseEntity.ok(exists));
    }
}
