package edu.pe.vallegrande.AuthenticationService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Person para el sistema de autenticación
 * Representa la información personal de las personas en el sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("persons")
public class Person {
    
    @Id
    private UUID id;
    
    @Column("document_type_id")
    private Integer documentTypeId;
    
    @Column("document_number")
    private String documentNumber;
    
    @Column("first_name")
    private String firstName;
    
    @Column("last_name")
    private String lastName;
    
    @Column("middle_name")
    private String middleName;
    
    @Column("birth_date")
    private LocalDate birthDate;
    
    @Column("gender")
    private String gender; // M, F
    
    @Column("personal_phone")
    private String personalPhone;
    
    @Column("work_phone")
    private String workPhone;
    
    @Column("personal_email")
    private String personalEmail;
    
    @Column("address")
    private String address;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}