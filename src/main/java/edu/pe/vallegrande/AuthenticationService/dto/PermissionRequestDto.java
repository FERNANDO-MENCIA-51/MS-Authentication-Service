package edu.pe.vallegrande.AuthenticationService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDto {

    @NotBlank(message = "Module is required")
    @Size(max = 50, message = "Module must be less than 50 characters")
    private String module;

    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action must be less than 50 characters")
    private String action;

    @Size(max = 100, message = "Resource must be less than 100 characters")
    private String resource;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}