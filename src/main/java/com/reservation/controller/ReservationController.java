package com.reservation.controller;

import com.reservation.model.dto.ReservationRequest;
import com.reservation.model.dto.ReservationResponse;
import com.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservas", description = "Endpoints para gestionar reservas de recursos")
@SecurityRequirement(name = "bearer-token")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    // POST /api/reservations - Crear una nueva reserva
    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(reservationService.createReservation(request, userEmail));
    }
    
    // GET /api/reservations/user - Ver mis reservas
    @GetMapping("/user")
    @Operation(summary = "Obtener todas las reservas del usuario autenticado")
    public ResponseEntity<List<ReservationResponse>> getUserReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(reservationService.getUserReservations(userEmail));
    }
    
    // GET /api/reservations/availability?resourceId=1&start=...&end=...
    @GetMapping("/availability")
    @Operation(summary = "Verificar disponibilidad de un recurso")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long resourceId,
            @RequestParam String start,
            @RequestParam String end) {
        
        // Convertir String a LocalDateTime
        java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(start);
        java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(end);
        
        boolean available = reservationService.isResourceAvailable(resourceId, startDateTime, endDateTime);
        return ResponseEntity.ok(available);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva existente")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelada por el usuario") String reason,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String userEmail = userDetails.getUsername();
        reservationService.cancelReservation(id, userEmail, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todas las reservas del sistema (solo ADMIN)")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}