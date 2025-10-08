package edu.pe.vallegrande.AuthenticationService.repository;

import edu.pe.vallegrande.AuthenticationService.model.Permission;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PermissionRepository extends R2dbcRepository<Permission, UUID> {
    Mono<Permission> findByModuleAndActionAndResource(String module, String action, String resource);
}