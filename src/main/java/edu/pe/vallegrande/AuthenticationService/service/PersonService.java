package edu.pe.vallegrande.AuthenticationService.service;

import edu.pe.vallegrande.AuthenticationService.dto.PersonRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.PersonResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz del servicio para la gestión de personas
 */
public interface PersonService {
    
    /**
     * Crear una nueva persona
     */
    Mono<PersonResponseDto> createPerson(PersonRequestDto personRequestDto);
    
    /**
     * Obtener todas las personas
     */
    Flux<PersonResponseDto> getAllPersons();
    
    /**
     * Obtener persona por ID
     */
    Mono<PersonResponseDto> getPersonById(UUID id);
    
    /**
     * Obtener persona por documento
     */
    Mono<PersonResponseDto> getPersonByDocument(Integer documentTypeId, String documentNumber);
    
    /**
     * Obtener persona por email
     */
    Mono<PersonResponseDto> getPersonByEmail(String email);
    
    /**
     * Buscar personas por nombre
     */
    Flux<PersonResponseDto> searchPersonsByName(String name);
    
    /**
     * Obtener personas por tipo de documento
     */
    Flux<PersonResponseDto> getPersonsByDocumentType(Integer documentTypeId);
    
    /**
     * Obtener personas por género
     */
    Flux<PersonResponseDto> getPersonsByGender(String gender);
    
    /**
     * Obtener personas por año de nacimiento
     */
    Flux<PersonResponseDto> getPersonsByBirthYear(Integer year);
    
    /**
     * Actualizar una persona
     */
    Mono<PersonResponseDto> updatePerson(UUID id, PersonRequestDto personRequestDto);
    
    /**
     * Eliminar una persona
     */
    Mono<Void> deletePerson(UUID id);
    
    /**
     * Verificar si existe una persona por documento
     */
    Mono<Boolean> existsByDocument(Integer documentTypeId, String documentNumber);
    
    /**
     * Verificar si existe una persona por email
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Obtener estadísticas de personas por género
     */
    Mono<Long> countByGender(String gender);
    
    /**
     * Obtener edad promedio
     */
    Mono<Double> getAverageAge();
}