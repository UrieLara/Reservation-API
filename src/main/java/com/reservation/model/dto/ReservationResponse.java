package com.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String userName;
    private String userEmail;
    private String resourceName;
    private Long resourceId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private String cancellationReason;
    private LocalDateTime createdAt;
}