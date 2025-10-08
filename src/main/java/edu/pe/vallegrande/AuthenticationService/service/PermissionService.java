package edu.pe.vallegrande.AuthenticationService.service;

import edu.pe.vallegrande.AuthenticationService.model.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PermissionService {
    Mono<Permission> createPermission(Permission permission);
    Mono<Permission> getPermissionById(UUID id);
    Mono<Permission> getPermissionByDetails(String module, String action, String resource);
    Flux<Permission> getAllPermissions();
    Mono<Permission> updatePermission(UUID id, Permission permission);
    // deletePermission method removed as per request
}