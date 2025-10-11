package edu.pe.vallegrande.AuthenticationService.service.impl;

import edu.pe.vallegrande.AuthenticationService.dto.PersonRequestDto;
import edu.pe.vallegrande.AuthenticationService.dto.PersonResponseDto;
import edu.pe.vallegrande.AuthenticationService.exception.ResourceNotFoundException;
import edu.pe.vallegrande.AuthenticationService.exception.DuplicateResourceException;
import edu.pe.vallegrande.AuthenticationService.model.Person;
import edu.pe.vallegrande.AuthenticationService.repository.PersonRepository;
import edu.pe.vallegrande.AuthenticationService.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

/**
 * Implementación del servicio para la gestión de personas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Mono<PersonResponseDto> createPerson(PersonRequestDto personRequestDto) {
        log.info("Creando nueva persona: {} {}", personRequestDto.getFirstName(), personRequestDto.getLastName());

        return personRepository.existsByDocumentTypeIdAndDocumentNumber(
                personRequestDto.getDocumentTypeId(),
                personRequestDto.getDocumentNumber())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateResourceException(
                                "Ya existe una persona con el documento: " + personRequestDto.getDocumentNumber()));
                    }

                    // Verificar email si se proporciona
                    if (personRequestDto.getPersonalEmail() != null
                            && !personRequestDto.getPersonalEmail().trim().isEmpty()) {
                        return personRepository.existsByPersonalEmail(personRequestDto.getPersonalEmail())
                                .flatMap(emailExists -> {
                                    if (emailExists) {
                                        return Mono.error(new DuplicateResourceException(
                                                "Ya existe una persona con el email: "
                                                        + personRequestDto.getPersonalEmail()));
                                    }
                                    return createPersonEntity(personRequestDto);
                                });
                    }

                    return createPersonEntity(personRequestDto);
                })
                .flatMap(personRepository::save)
                .map(this::mapToResponseDto)
                .doOnSuccess(person -> log.info("Persona creada exitosamente: {}", person.getFullName()))
                .doOnError(error -> log.error("Error al crear persona: {}", error.getMessage()));
    }

    @Override
    public Flux<PersonResponseDto> getAllPersons() {
        log.info("Obteniendo todas las personas");
        return personRepository.findAll()
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> getAllActivePersons() {
        log.info("Obteniendo todas las personas activas");
        return personRepository.findAllActive()
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> getAllInactivePersons() {
        log.info("Obteniendo todas las personas inactivas");
        return personRepository.findAllInactive()
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<PersonResponseDto> getPersonById(UUID id) {
        log.info("Obteniendo persona por ID: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Persona no encontrada con ID: " + id)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<PersonResponseDto> getPersonByDocument(Integer documentTypeId, String documentNumber) {
        log.info("Obteniendo persona por documento: {} - {}", documentTypeId, documentNumber);
        return personRepository.findByDocumentTypeIdAndDocumentNumber(documentTypeId, documentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Persona no encontrada con documento: " + documentNumber)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<PersonResponseDto> getPersonByEmail(String email) {
        log.info("Obteniendo persona por email: {}", email);
        return personRepository.findByPersonalEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Persona no encontrada con email: " + email)))
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> searchPersonsByName(String name) {
        log.info("Buscando personas por nombre: {}", name);
        return personRepository.findByNameContaining(name)
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> getPersonsByDocumentType(Integer documentTypeId) {
        log.info("Obteniendo personas por tipo de documento: {}", documentTypeId);
        return personRepository.findByDocumentTypeId(documentTypeId)
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> getPersonsByGender(String gender) {
        log.info("Obteniendo personas por género: {}", gender);
        return personRepository.findByGender(gender)
                .map(this::mapToResponseDto);
    }

    @Override
    public Flux<PersonResponseDto> getPersonsByBirthYear(Integer year) {
        log.info("Obteniendo personas por año de nacimiento: {}", year);
        return personRepository.findByBirthYear(year)
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<PersonResponseDto> updatePerson(UUID id, PersonRequestDto personRequestDto) {
        log.info("Actualizando persona con ID: {}", id);

        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Persona no encontrada con ID: " + id)))
                .flatMap(existingPerson -> {
                    // Verificar documento si cambió
                    if (!existingPerson.getDocumentTypeId().equals(personRequestDto.getDocumentTypeId()) ||
                            !existingPerson.getDocumentNumber().equals(personRequestDto.getDocumentNumber())) {

                        return personRepository.existsByDocumentAndIdNot(
                                personRequestDto.getDocumentTypeId(),
                                personRequestDto.getDocumentNumber(),
                                id)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new DuplicateResourceException(
                                                "Ya existe una persona con el documento: "
                                                        + personRequestDto.getDocumentNumber()));
                                    }
                                    return Mono.just(existingPerson);
                                });
                    }
                    return Mono.just(existingPerson);
                })
                .flatMap(existingPerson -> {
                    // Verificar email si cambió
                    if (personRequestDto.getPersonalEmail() != null &&
                            !personRequestDto.getPersonalEmail().equals(existingPerson.getPersonalEmail())) {

                        return personRepository.existsByEmailAndIdNot(personRequestDto.getPersonalEmail(), id)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new DuplicateResourceException(
                                                "Ya existe una persona con el email: "
                                                        + personRequestDto.getPersonalEmail()));
                                    }
                                    return Mono.just(existingPerson);
                                });
                    }
                    return Mono.just(existingPerson);
                })
                .flatMap(existingPerson -> {
                    Person updatedPerson = Person.builder()
                            .id(existingPerson.getId())
                            .documentTypeId(personRequestDto.getDocumentTypeId())
                            .documentNumber(personRequestDto.getDocumentNumber())
                            .firstName(personRequestDto.getFirstName())
                            .lastName(personRequestDto.getLastName())
                            .middleName(personRequestDto.getMiddleName())
                            .birthDate(personRequestDto.getBirthDate())
                            .gender(personRequestDto.getGender())
                            .personalPhone(personRequestDto.getPersonalPhone())
                            .workPhone(personRequestDto.getWorkPhone())
                            .personalEmail(personRequestDto.getPersonalEmail())
                            .address(personRequestDto.getAddress())
                            .status(existingPerson.getStatus())
                            .createdAt(existingPerson.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return personRepository.save(updatedPerson);
                })
                .map(this::mapToResponseDto);
    }

    @Override
    public Mono<PersonResponseDto> deletePerson(UUID id) {
        log.info("Eliminando persona (borrado lógico) con ID: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Persona no encontrada con ID: " + id)))
                .flatMap(person -> {
                    person.setStatus(false);
                    person.setUpdatedAt(LocalDateTime.now());
                    return personRepository.save(person);
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(p -> log.info("Persona eliminada (status=false) con ID: {}", id));
    }

    @Override
    public Mono<PersonResponseDto> restorePerson(UUID id) {
        log.info("Restaurando persona con ID: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Persona no encontrada con ID: " + id)))
                .flatMap(person -> {
                    person.setStatus(true);
                    person.setUpdatedAt(LocalDateTime.now());
                    return personRepository.save(person);
                })
                .map(this::mapToResponseDto)
                .doOnSuccess(p -> log.info("Persona restaurada (status=true) con ID: {}", id));
    }

    @Override
    public Mono<Boolean> existsByDocument(Integer documentTypeId, String documentNumber) {
        return personRepository.existsByDocumentTypeIdAndDocumentNumber(documentTypeId, documentNumber);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return personRepository.existsByPersonalEmail(email);
    }

    @Override
    public Mono<Long> countByGender(String gender) {
        return personRepository.countByGender(gender);
    }

    @Override
    public Mono<Double> getAverageAge() {
        return personRepository.getAverageAge();
    }

    /**
     * Crear entidad Person desde DTO
     */
    private Mono<Person> createPersonEntity(PersonRequestDto dto) {
        return Mono.just(Person.builder()
                .id(UUID.randomUUID())
                .documentTypeId(dto.getDocumentTypeId())
                .documentNumber(dto.getDocumentNumber())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .personalPhone(dto.getPersonalPhone())
                .workPhone(dto.getWorkPhone())
                .personalEmail(dto.getPersonalEmail())
                .address(dto.getAddress())
                .status(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    /**
     * Mapea una entidad Person a PersonResponseDto
     */
    private PersonResponseDto mapToResponseDto(Person person) {
        return PersonResponseDto.builder()
                .id(person.getId())
                .documentTypeId(person.getDocumentTypeId())
                .documentNumber(person.getDocumentNumber())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .middleName(person.getMiddleName())
                .fullName(buildFullName(person))
                .birthDate(person.getBirthDate())
                .age(calculateAge(person.getBirthDate()))
                .gender(person.getGender())
                .personalPhone(person.getPersonalPhone())
                .workPhone(person.getWorkPhone())
                .personalEmail(person.getPersonalEmail())
                .address(person.getAddress())
                .status(person.getStatus())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .build();
    }

    /**
     * Construir nombre completo
     */
    private String buildFullName(Person person) {
        StringBuilder fullName = new StringBuilder();
        fullName.append(person.getFirstName());

        if (person.getMiddleName() != null && !person.getMiddleName().trim().isEmpty()) {
            fullName.append(" ").append(person.getMiddleName());
        }

        fullName.append(" ").append(person.getLastName());
        return fullName.toString();
    }

    /**
     * Calcular edad
     */
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}