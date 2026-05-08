package com.reservation.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    
    @NotNull(message = "El ID del recurso es requerido")
    private Long resourceId;
    
    @NotNull(message = "La fecha y hora de inicio es requerida")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "La fecha y hora de fin es requerida")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime endDateTime;
}