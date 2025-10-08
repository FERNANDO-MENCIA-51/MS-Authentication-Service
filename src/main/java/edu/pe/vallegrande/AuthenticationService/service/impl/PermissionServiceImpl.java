package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.exception.DuplicateResourceException;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.model.Permission;
import edu.pe.vallegrande.AuthenticationService.repository.PermissionRepository;
import edu.pe.vallegrande.AuthenticationService.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Mono<Permission> createPermission(Permission permission) {
        permission.setId(UUID.randomUUID());
        permission.setCreatedAt(LocalDateTime.now());
        
        // Check if permission with same module, action, and resource already exists
        return permissionRepository.findByModuleAndActionAndResource(
                permission.getModule(), permission.getAction(), permission.getResource())
            .flatMap(existing -> Mono.error(new DuplicateResourceException("Permission already exists with these details")))
            .switchIfEmpty(Mono.defer(() -> permissionRepository.save(permission)))
            .cast(Permission.class);
    }

    @Override
    public Mono<Permission> getPermissionById(UUID id) {
        return permissionRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with id: " + id)));
    }

    @Override
    public Mono<Permission> getPermissionByDetails(String module, String action, String resource) {
        return permissionRepository.findByModuleAndActionAndResource(module, action, resource)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                "Permission not found with module: " + module + ", action: " + action + ", resource: " + resource)));
    }

    @Override
    public Flux<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Mono<Permission> updatePermission(UUID id, Permission permission) {
        return permissionRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Permission not found with id: " + id)))
            .flatMap(existing -> {
                existing.setModule(permission.getModule());
                existing.setAction(permission.getAction());
                existing.setResource(permission.getResource());
                existing.setDescription(permission.getDescription());
                // Note: We don't update the createdAt timestamp or createdBy fields
                return permissionRepository.save(existing);
            });
    }

}