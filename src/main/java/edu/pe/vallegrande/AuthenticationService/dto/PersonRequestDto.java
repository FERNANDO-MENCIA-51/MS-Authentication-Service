package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para las solicitudes de creación y actualización de personas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {
    
    private Integer documentTypeId;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private String gender; // M, F
    private String personalPhone;
    private String workPhone;
    private String personalEmail;
    private String address;
}