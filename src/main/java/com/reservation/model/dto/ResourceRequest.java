package com.reservation.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequest {
    
    @NotBlank(message = "El nombre del recurso es requerido")
    private String name;
    
    private String description;
    
    @NotNull(message = "La capacidad es requerida")
    @Min(value = 1, message = "La capacidad mínima es 1 persona")
    private Integer capacity;
    
    private String location;
}