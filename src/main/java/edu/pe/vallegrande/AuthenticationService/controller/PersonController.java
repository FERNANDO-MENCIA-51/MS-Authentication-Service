package edu.pe.vallegrande.AuthenticationService.controller;

import edu.pe.vallegrande.AuthenticationService.dto.PersonRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.PersonResponseDto;
import edu.pe.vallegrande.AuthenticationService.service.PersonService;
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
 * Controlador REST para la gestión de personas
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
@Tag(name = "Persons", description = "API para la gestión de personas del sistema")
public class PersonController {
    
    private final PersonService personService;
    
    @Operation(summary = "Crear una nueva persona")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Persona creada exitosamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe una persona con ese documento o email")
    })
    @PostMapping
    public Mono<ResponseEntity<PersonResponseDto>> createPerson(@RequestBody PersonRequestDto personRequestDto) {
        log.info("Solicitud para crear persona: {} {}", personRequestDto.getFirstName(), personRequestDto.getLastName());
        return personService.createPerson(personRequestDto)
                .map(person -> ResponseEntity.status(HttpStatus.CREATED).body(person));
    }
    
    @Operation(summary = "Obtener todas las personas")
    @GetMapping
    public Flux<PersonResponseDto> getAllPersons() {
        log.info("Solicitud para obtener todas las personas");
        return personService.getAllPersons();
    }
    
    @Operation(summary = "Obtener todas las personas activas")
    @GetMapping("/active")
    public Flux<PersonResponseDto> getAllActivePersons() {
        log.info("Solicitud para obtener todas las personas activas");
        return personService.getAllActivePersons();
    }
    
    @Operation(summary = "Obtener todas las personas inactivas")
    @GetMapping("/inactive")
    public Flux<PersonResponseDto> getAllInactivePersons() {
        log.info("Solicitud para obtener todas las personas inactivas");
        return personService.getAllInactivePersons();
    }
    
    @Operation(summary = "Obtener persona por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona encontrada"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PersonResponseDto>> getPersonById(
            @Parameter(description = "ID de la persona") @PathVariable UUID id) {
        log.info("Solicitud para obtener persona por ID: {}", id);
        return personService.getPersonById(id)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Obtener persona por documento")
    @GetMapping("/document/{documentTypeId}/{documentNumber}")
    public Mono<ResponseEntity<PersonResponseDto>> getPersonByDocument(
            @Parameter(description = "Tipo de documento") @PathVariable Integer documentTypeId,
            @Parameter(description = "Número de documento") @PathVariable String documentNumber) {
        log.info("Solicitud para obtener persona por documento: {} - {}", documentTypeId, documentNumber);
        return personService.getPersonByDocument(documentTypeId, documentNumber)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Obtener persona por email")
    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<PersonResponseDto>> getPersonByEmail(
            @Parameter(description = "Email personal") @PathVariable String email) {
        log.info("Solicitud para obtener persona por email: {}", email);
        return personService.getPersonByEmail(email)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Buscar personas por nombre")
    @GetMapping("/search/name/{name}")
    public Flux<PersonResponseDto> searchPersonsByName(
            @Parameter(description = "Nombre a buscar") @PathVariable String name) {
        log.info("Solicitud para buscar personas por nombre: {}", name);
        return personService.searchPersonsByName(name);
    }
    
    @Operation(summary = "Obtener personas por tipo de documento")
    @GetMapping("/document-type/{documentTypeId}")
    public Flux<PersonResponseDto> getPersonsByDocumentType(
            @Parameter(description = "Tipo de documento") @PathVariable Integer documentTypeId) {
        log.info("Solicitud para obtener personas por tipo de documento: {}", documentTypeId);
        return personService.getPersonsByDocumentType(documentTypeId);
    }
    
    @Operation(summary = "Obtener personas por género")
    @GetMapping("/gender/{gender}")
    public Flux<PersonResponseDto> getPersonsByGender(
            @Parameter(description = "Género (M/F)") @PathVariable String gender) {
        log.info("Solicitud para obtener personas por género: {}", gender);
        return personService.getPersonsByGender(gender);
    }
    
    @Operation(summary = "Obtener personas por año de nacimiento")
    @GetMapping("/birth-year/{year}")
    public Flux<PersonResponseDto> getPersonsByBirthYear(
            @Parameter(description = "Año de nacimiento") @PathVariable Integer year) {
        log.info("Solicitud para obtener personas por año de nacimiento: {}", year);
        return personService.getPersonsByBirthYear(year);
    }
    
    @Operation(summary = "Actualizar una persona")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PersonResponseDto>> updatePerson(
            @Parameter(description = "ID de la persona") @PathVariable UUID id,
            @RequestBody PersonRequestDto personRequestDto) {
        log.info("Solicitud para actualizar persona con ID: {}", id);
        return personService.updatePerson(id, personRequestDto)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Eliminar una persona (borrado lógico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<PersonResponseDto>> deletePerson(
            @Parameter(description = "ID de la persona") @PathVariable UUID id) {
        log.info("Solicitud para eliminar persona con ID: {}", id);
        return personService.deletePerson(id)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Restaurar una persona eliminada")
    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<PersonResponseDto>> restorePerson(
            @Parameter(description = "ID de la persona") @PathVariable UUID id) {
        log.info("Solicitud para restaurar persona con ID: {}", id);
        return personService.restorePerson(id)
                .map(person -> ResponseEntity.ok(person));
    }
    
    @Operation(summary = "Verificar si existe persona por documento")
    @GetMapping("/exists/document/{documentTypeId}/{documentNumber}")
    public Mono<ResponseEntity<Boolean>> existsByDocument(
            @Parameter(description = "Tipo de documento") @PathVariable Integer documentTypeId,
            @Parameter(description = "Número de documento") @PathVariable String documentNumber) {
        log.info("Verificando existencia de persona con documento: {} - {}", documentTypeId, documentNumber);
        return personService.existsByDocument(documentTypeId, documentNumber)
                .map(exists -> ResponseEntity.ok(exists));
    }
    
    @Operation(summary = "Verificar si existe persona por email")
    @GetMapping("/exists/email/{email}")
    public Mono<ResponseEntity<Boolean>> existsByEmail(
            @Parameter(description = "Email personal") @PathVariable String email) {
        log.info("Verificando existencia de persona con email: {}", email);
        return personService.existsByEmail(email)
                .map(exists -> ResponseEntity.ok(exists));
    }
    
    @Operation(summary = "Contar personas por género")
    @GetMapping("/stats/gender/{gender}")
    public Mono<ResponseEntity<Long>> countByGender(
            @Parameter(description = "Género (M/F)") @PathVariable String gender) {
        log.info("Contando personas por género: {}", gender);
        return personService.countByGender(gender)
                .map(count -> ResponseEntity.ok(count));
    }
    
    @Operation(summary = "Obtener edad promedio")
    @GetMapping("/stats/average-age")
    public Mono<ResponseEntity<Double>> getAverageAge() {
        log.info("Obteniendo edad promedio");
        return personService.getAverageAge()
                .map(age -> ResponseEntity.ok(age));
    }
}