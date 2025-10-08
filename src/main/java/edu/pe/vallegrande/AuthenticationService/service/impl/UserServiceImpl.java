package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.dto.UserRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.UserResponseDto;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.exception.DuplicateResourceException;
import edu.pe.vallegrande.AuthenticationService.model.User;
import edu.pe.vallegrande.AuthenticationService.repository.UserRepository;
import edu.pe.vallegrande.AuthenticationService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Implementación del servicio para la gestión de usuarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        log.info("Creando nuevo usuario: {}", userRequestDto.getUsername());

        return userRepository.existsByUsername(userRequestDto.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException(
                                "Ya existe un usuario con el username: " + userRequestDto.getUsername()));
                    }

                    // Convertir Map a String para almacenar en la base de datos
                    String preferencesAsString = convertMapToString(userRequestDto.getPreferences());

                    User user = User.builder()
                            .id(UUID.randomUUID())
                            .username(userRequestDto.getUsername())
                            .passwordHash(hashPassword(userRequestDto.getPassword()))
                            .personId(userRequestDto.getPersonId())
                            .areaId(userRequestDto.getAreaId())
                            .positionId(userRequestDto.getPositionId())
                            .directManagerId(userRequestDto.getDirectManagerId())
                            .status(userRequestDto.getStatus() != null ? userRequestDto.getStatus() : "ACTIVE")
                            .loginAttempts(0)
                            .preferences(preferencesAsString)
                            .createdBy(userRequestDto.getCreatedBy())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .version(1)
                            .build();

                    return userRepository.save(user);
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(user -> log.info("Usuario creado exitosamente: {}", user.getUsername()))
                .doOnError(error -> log.error("Error al crear usuario: {}", error.getMessage()));
    }

    @Override
    public Flux<UserResponseDto> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return userRepository.findAll()
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getActiveUsers() {
        log.info("Obteniendo usuarios activos");
        return userRepository.findActiveUsers()
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getInactiveUsers() {
        log.info("Obteniendo usuarios inactivos");
        return userRepository.findInactiveUsers()
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getSuspendedUsers() {
        log.info("Obteniendo usuarios suspendidos");
        return userRepository.findSuspendedUsers()
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> getUserById(UUID id) {
        log.info("Obteniendo usuario por ID: {}", id);
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado con ID: " + id)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> getUserByUsername(String username) {
        log.info("Obteniendo usuario por username: {}", username);
        return userRepository.findByUsername(username)
                .switchIfEmpty(
                        Mono.error(new ResourceNotFoundException("Usuario no encontrado con username: " + username)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getUsersByArea(UUID areaId) {
        log.info("Obteniendo usuarios por área: {}", areaId);
        return userRepository.findByAreaId(areaId)
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getUsersByPosition(UUID positionId) {
        log.info("Obteniendo usuarios por posición: {}", positionId);
        return userRepository.findByPositionId(positionId)
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<UserResponseDto> getUsersByManager(UUID managerId) {
        log.info("Obteniendo usuarios por manager: {}", managerId);
        return userRepository.findByDirectManagerId(managerId)
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> updateUser(UUID id, UserRequestDto userRequestDto) {
        log.info("Actualizando usuario con ID: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuario no encontrado con ID: " + id)))
                .flatMap(existingUser -> {
                    // Verificar si el username ya existe en otro usuario
                    if (!existingUser.getUsername().equals(userRequestDto.getUsername())) {
                        return userRepository.existsByUsernameAndIdNot(userRequestDto.getUsername(), id)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(
                                                new DuplicateResourceException("Ya existe un usuario con el username: "
                                                        + userRequestDto.getUsername()));
                                    }
                                    return Mono.just(existingUser);
                                });
                    }
                    return Mono.just(existingUser);
                })
                .flatMap(existingUser -> {
                    // Convertir Map a String para almacenar en la base de datos
                    String preferencesAsString = userRequestDto.getPreferences() != null 
                        ? convertMapToString(userRequestDto.getPreferences()) 
                        : existingUser.getPreferences();

                    User updatedUser = User.builder()
                            .id(existingUser.getId())
                            .username(userRequestDto.getUsername())
                            .passwordHash(
                                    userRequestDto.getPassword() != null ? hashPassword(userRequestDto.getPassword())
                                            : existingUser.getPasswordHash())
                            .personId(userRequestDto.getPersonId() != null ? userRequestDto.getPersonId()
                                    : existingUser.getPersonId())
                            .areaId(userRequestDto.getAreaId() != null ? userRequestDto.getAreaId()
                                    : existingUser.getAreaId())
                            .positionId(userRequestDto.getPositionId() != null ? userRequestDto.getPositionId()
                                    : existingUser.getPositionId())
                            .directManagerId(userRequestDto.getDirectManagerId())
                            .status(userRequestDto.getStatus() != null ? userRequestDto.getStatus()
                                    : existingUser.getStatus())
                            .lastLogin(existingUser.getLastLogin())
                            .loginAttempts(existingUser.getLoginAttempts())
                            .blockedUntil(existingUser.getBlockedUntil())
                            .preferences(preferencesAsString)
                            .createdBy(existingUser.getCreatedBy())
                            .createdAt(existingUser.getCreatedAt())
                            .updatedBy(userRequestDto.getUpdatedBy())
                            .updatedAt(LocalDateTime.now())
                            .version(existingUser.getVersion() + 1)
                            .build();

                    return userRepository.save(updatedUser);
                })
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> changeUserStatus(UUID id, String status, UUID updatedBy) {
        log.info("Cambiando status del usuario {} a: {}", id, status);
        return userRepository.updateStatus(id, status, updatedBy)
                .then(userRepository.findById(id))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<Void> deleteUser(UUID id, UUID updatedBy) {
        log.info("Eliminando usuario con ID: {}", id);
        return userRepository.updateStatus(id, "INACTIVE", updatedBy)
                .then();
    }

    @Override
    public Mono<UserResponseDto> restoreUser(UUID id, UUID updatedBy) {
        log.info("Restaurando usuario con ID: {}", id);
        return userRepository.updateStatus(id, "ACTIVE", updatedBy)
                .then(userRepository.findById(id))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> suspendUser(UUID id, UUID updatedBy) {
        log.info("Suspendiendo usuario con ID: {}", id);
        return userRepository.updateStatus(id, "SUSPENDED", updatedBy)
                .then(userRepository.findById(id))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> blockUser(UUID id) {
        log.info("Bloqueando usuario con ID: {}", id);
        LocalDateTime blockedUntil = LocalDateTime.now().plusHours(24); // Bloquear por 24 horas
        return userRepository.blockUser(id, blockedUntil)
                .then(userRepository.findById(id))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<UserResponseDto> unblockUser(UUID id) {
        log.info("Desbloqueando usuario con ID: {}", id);
        return userRepository.unblockUser(id)
                .then(userRepository.findById(id))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Mono<Void> updateLastLogin(UUID id) {
        return userRepository.updateLastLogin(id, LocalDateTime.now())
                .then();
    }

    @Override
    public Mono<Void> incrementLoginAttempts(UUID id) {
        return userRepository.incrementLoginAttempts(id)
                .then();
    }

    /**
     * Hash de password usando BCrypt
     */
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Convertir Map<String, Object> a String
     */
    private String convertMapToString(Map<String, Object> map) {
        if (map == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Error convirtiendo Map a String: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Convertir String a Map<String, Object>
     */
    private Map<String, Object> convertStringToMap(String str) {
        if (str == null || str.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(str, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error convirtiendo String a Map: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Mapea una entidad User a UserResponseDto
     */
    private UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personId(user.getPersonId())
                .areaId(user.getAreaId())
                .positionId(user.getPositionId())
                .directManagerId(user.getDirectManagerId())
                .status(user.getStatus())
                .lastLogin(user.getLastLogin())
                .loginAttempts(user.getLoginAttempts())
                .blockedUntil(user.getBlockedUntil())
                .preferences(convertStringToMap(user.getPreferences()))
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .version(user.getVersion())
                .build();
    }
}