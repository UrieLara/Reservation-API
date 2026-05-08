package com.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResourceResponse {
    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
}