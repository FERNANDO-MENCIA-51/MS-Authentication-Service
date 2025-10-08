package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.PermissionRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.PermissionResponseDto;
import edu.pe.vallegrande.AuthenticationService.model.Permission;
import edu.pe.vallegrande.AuthenticationService.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public Mono<ResponseEntity<PermissionResponseDto>> createPermission(
            @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        Permission permission = Permission.builder()
                .module(permissionRequestDto.getModule())
                .action(permissionRequestDto.getAction())
                .resource(permissionRequestDto.getResource())
                .description(permissionRequestDto.getDescription())
                .build();

        return permissionService.createPermission(permission)
                .map(this::mapToResponseDto)
                .map(responseDto -> ResponseEntity.status(HttpStatus.CREATED).body(responseDto))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PermissionResponseDto>> getPermissionById(@PathVariable UUID id) {
        return permissionService.getPermissionById(id)
                .map(this::mapToResponseDto)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<PermissionResponseDto> getAllPermissions() {
        return permissionService.getAllPermissions()
                .map(this::mapToResponseDto);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PermissionResponseDto>> updatePermission(
            @PathVariable UUID id,
            @Valid @RequestBody PermissionRequestDto permissionRequestDto) {
        Permission permission = Permission.builder()
                .module(permissionRequestDto.getModule())
                .action(permissionRequestDto.getAction())
                .resource(permissionRequestDto.getResource())
                .description(permissionRequestDto.getDescription())
                .build();

        return permissionService.updatePermission(id, permission)
                .map(this::mapToResponseDto)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    // DELETE endpoint removed as per request

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