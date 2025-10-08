package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {

    private UUID id;
    private String module;
    private String action;
    private String resource;
    private String description;
    private LocalDateTime createdAt;
    private UUID createdBy;
}