package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.PermissionRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.PermissionResponseDto;
import edu.pe.vallegrande.AuthenticationService.model.Permission;
import edu.pe.vallegrande.AuthenticationService.service.PermissionService;
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

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Controlador REST para la gestión de permisos del sistema
 * 
 * Este controlador maneja todas las operaciones CRUD para los permisos,
 * incluyendo la creación, consulta, actualización y eliminación.
 * Los permisos definen las acciones que pueden realizar los roles en el sistema.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "API para la gestión de permisos del sistema")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Crear un nuevo permiso en el sistema
     * 
     * @param permissionRequestDto Datos del permiso a crear
     * @return Permiso creado con su ID generado
     */
    @Operation(
        summary = "Crear nuevo permiso",
        description = "Crea un nuevo permiso en el sistema con módulo, acción y recurso específicos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El permiso ya existe")
    })
    @PostMapping
    public Mono<ResponseEntity<PermissionResponseDto>> createPermission(
            @Parameter(description = "Datos del permiso a crear", required = true)
            @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        
        log.info("Creando nuevo permiso: {}:{}:{}", 
                permissionRequestDto.getModule(), 
                permissionRequestDto.getAction(), 
                permissionRequestDto.getResource());
        
        Permission permission = Permission.builder()
                .module(permissionRequestDto.getModule())
                .action(permissionRequestDto.getAction())
                .resource(permissionRequestDto.getResource())
                .description(permissionRequestDto.getDescription())
                .build();

        return permissionService.createPermission(permission)
                .map(this::mapToResponseDto)
                .map(responseDto -> {
                    log.info("Permiso creado exitosamente con ID: {}", responseDto.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
                })
                .doOnError(error -> log.error("Error al crear permiso: {}", error.getMessage()));
    }

    /**
     * Obtener un permiso por su ID
     * 
     * @param id ID único del permiso
     * @return Datos completos del permiso
     */
    @Operation(
        summary = "Obtener permiso por ID",
        description = "Recupera la información completa de un permiso específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso encontrado"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PermissionResponseDto>> getPermissionById(
            @Parameter(description = "ID único del permiso", required = true)
            @PathVariable UUID id) {
        
        log.info("Consultando permiso con ID: {}", id);
        
        return permissionService.getPermissionById(id)
                .map(this::mapToResponseDto)
                .map(responseDto -> {
                    log.info("Permiso encontrado: {}", responseDto.getId());
                    return ResponseEntity.ok(responseDto);
                })
                .doOnError(error -> log.error("Error al consultar permiso {}: {}", id, error.getMessage()));
    }

    /**
     * Obtener todos los permisos del sistema
     * 
     * @return Lista completa de permisos disponibles
     */
    @Operation(
        summary = "Listar todos los permisos",
        description = "Obtiene la lista completa de permisos disponibles en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de permisos obtenida exitosamente")
    })
    @GetMapping
    public Flux<PermissionResponseDto> getAllPermissions() {
        log.info("Consultando todos los permisos del sistema");
        
        return permissionService.getAllPermissions()
                .map(this::mapToResponseDto)
                .doOnComplete(() -> log.info("Consulta de permisos completada"))
                .doOnError(error -> log.error("Error al consultar permisos: {}", error.getMessage()));
    }

    /**
     * Actualizar un permiso existente
     * 
     * @param id ID del permiso a actualizar
     * @param permissionRequestDto Nuevos datos del permiso
     * @return Permiso actualizado
     */
    @Operation(
        summary = "Actualizar permiso",
        description = "Actualiza la información de un permiso existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PermissionResponseDto>> updatePermission(
            @Parameter(description = "ID del permiso a actualizar", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos del permiso", required = true)
            @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        
        log.info("Actualizando permiso con ID: {}", id);
        
        Permission permission = Permission.builder()
                .module(permissionRequestDto.getModule())
                .action(permissionRequestDto.getAction())
                .resource(permissionRequestDto.getResource())
                .description(permissionRequestDto.getDescription())
                .build();

        return permissionService.updatePermission(id, permission)
                .map(this::mapToResponseDto)
                .map(responseDto -> {
                    log.info("Permiso actualizado exitosamente: {}", id);
                    return ResponseEntity.ok(responseDto);
                })
                .doOnError(error -> log.error("Error al actualizar permiso {}: {}", id, error.getMessage()));
    }

    /**
     * Eliminar permiso
     * 
     * @param id ID del permiso a eliminar
     * @return Confirmación de eliminación
     */
    @Operation(
        summary = "Eliminar permiso",
        description = "Elimina un permiso del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Permiso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePermission(
            @Parameter(description = "ID del permiso a eliminar", required = true)
            @PathVariable UUID id) {
        
        log.info("Eliminando permiso con ID: {}", id);
        
        return permissionService.deletePermission(id)
                .then(Mono.fromCallable(() -> {
                    log.info("Permiso eliminado exitosamente: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                }))
                .doOnError(error -> log.error("Error al eliminar permiso {}: {}", id, error.getMessage()));
    }

    /**
     * Buscar permiso por detalles específicos
     * 
     * @param module Módulo del permiso
     * @param action Acción del permiso
     * @param resource Recurso del permiso (opcional)
     * @return Permiso que coincide con los criterios
     */
    @Operation(
        summary = "Buscar permiso por detalles",
        description = "Busca un permiso específico por módulo, acción y recurso"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso encontrado"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @GetMapping("/search")
    public Mono<ResponseEntity<PermissionResponseDto>> getPermissionByDetails(
            @Parameter(description = "Módulo del permiso", required = true)
            @RequestParam String module,
            @Parameter(description = "Acción del permiso", required = true)
            @RequestParam String action,
            @Parameter(description = "Recurso del permiso")
            @RequestParam(required = false) String resource) {
        
        log.info("Buscando permiso: {}:{}:{}", module, action, resource);
        
        return permissionService.getPermissionByDetails(module, action, resource)
                .map(this::mapToResponseDto)
                .map(responseDto -> {
                    log.info("Permiso encontrado: {}", responseDto.getId());
                    return ResponseEntity.ok(responseDto);
                })
                .doOnError(error -> log.error("Error al buscar permiso {}:{}:{}: {}", 
                    module, action, resource, error.getMessage()));
    }

    /**
     * Obtener permisos por módulo
     * 
     * @param module Nombre del módulo
     * @return Lista de permisos del módulo especificado
     */
    @Operation(
        summary = "Obtener permisos por módulo",
        description = "Recupera todos los permisos asociados a un módulo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permisos del módulo obtenidos exitosamente")
    })
    @GetMapping("/module/{module}")
    public Flux<PermissionResponseDto> getPermissionsByModule(
            @Parameter(description = "Nombre del módulo", required = true)
            @PathVariable String module) {
        
        log.info("Consultando permisos del módulo: {}", module);
        
        return permissionService.getPermissionsByModule(module)
                .map(this::mapToResponseDto)
                .doOnComplete(() -> log.info("Consulta de permisos del módulo {} completada", module))
                .doOnError(error -> log.error("Error al consultar permisos del módulo {}: {}", 
                    module, error.getMessage()));
    }

    /**
     * Verificar si existe un permiso
     * 
     * @param module Módulo del permiso
     * @param action Acción del permiso
     * @param resource Recurso del permiso (opcional)
     * @return true si el permiso existe, false en caso contrario
     */
    @Operation(
        summary = "Verificar existencia de permiso",
        description = "Verifica si existe un permiso con los detalles especificados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada")
    })
    @GetMapping("/exists")
    public Mono<ResponseEntity<Boolean>> existsPermission(
            @Parameter(description = "Módulo del permiso", required = true)
            @RequestParam String module,
            @Parameter(description = "Acción del permiso", required = true)
            @RequestParam String action,
            @Parameter(description = "Recurso del permiso")
            @RequestParam(required = false) String resource) {
        
        log.info("Verificando existencia de permiso: {}:{}:{}", module, action, resource);
        
        return permissionService.existsPermission(module, action, resource)
                .map(exists -> {
                    log.info("Permiso {}:{}:{} existe: {}", module, action, resource, exists);
                    return ResponseEntity.ok(exists);
                })
                .doOnError(error -> log.error("Error al verificar permiso {}:{}:{}: {}", 
                    module, action, resource, error.getMessage()));
    }

    /**
     * Mapea una entidad Permission a su DTO de respuesta
     */
    private PermissionResponseDto mapToResponseDto(Permission permission) {
        return PermissionResponseDto.builder()
                .id(permission.getId())
                .module(permission.getModule())
                .action(permission.getAction())
                .resource(permission.getResource())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .createdBy(permission.getCreatedBy())
                .build();
    }
}
