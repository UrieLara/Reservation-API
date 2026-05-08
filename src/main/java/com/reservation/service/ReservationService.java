package com.reservation.service;

import com.reservation.model.dto.ReservationRequest;
import com.reservation.model.dto.ReservationResponse;
import com.reservation.model.entity.Reservation;
import com.reservation.model.entity.Resource;
import com.reservation.model.entity.User;
import com.reservation.model.enums.ReservationStatus;
import com.reservation.repository.ReservationRepository;
import com.reservation.repository.ResourceRepository;
import com.reservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    
    public ReservationService(ReservationRepository reservationRepository,
                              ResourceRepository resourceRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }
    
    // Crear una nueva reserva
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String userEmail) {
        
        // 1. Validar que el recurso existe y está activo
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado con ID: " + request.getResourceId()));
        
        if (!resource.getIsActive()) {
            throw new RuntimeException("El recurso no está disponible");
        }
        
        // 2. Validar que el usuario existe
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 3. Validar que las fechas son correctas
        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw new RuntimeException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        
        if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar en el pasado");
        }
        
        // 4. Validar que no hay conflictos de horario (¡CORAZÓN DE LA LÓGICA!)
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                resource, request.getStartDateTime(), request.getEndDateTime());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Conflicto de horario: El recurso ya está reservado en ese período");
        }
        
        // 5. Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartDateTime(request.getStartDateTime());
        reservation.setEndDateTime(request.getEndDateTime());
        reservation.setStatus(ReservationStatus.ACTIVE);
        
        Reservation saved = reservationRepository.save(reservation);
        
        return toResponse(saved);
    }
    
    // Obtener reservas del usuario actual
    public List<ReservationResponse> getUserReservations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<Reservation> reservations = reservationRepository.findByUserIdAndStatus(
                user.getId(), ReservationStatus.ACTIVE);
        
        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    // Verificar disponibilidad de un recurso en una fecha
    public boolean isResourceAvailable(Long resourceId, LocalDateTime start, LocalDateTime end) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                resource, start, end);
        
        return conflicts.isEmpty();
    }
    
    // Convertir Entity a DTO
    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getName(),
                reservation.getUser().getEmail(),
                reservation.getResource().getName(),
                reservation.getResource().getId(),
                reservation.getStartDateTime(),
                reservation.getEndDateTime(),
                reservation.getStatus().name(),
                reservation.getCancellationReason(),
                reservation.getCreatedAt()
        );
    }
}