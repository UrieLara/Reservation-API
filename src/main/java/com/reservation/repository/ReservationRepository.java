package com.reservation.repository;

import com.reservation.model.entity.Reservation;
import com.reservation.model.entity.Resource;
import com.reservation.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Buscar reservas activas de un usuario
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
    
    // Buscar reservas activas de un recurso
    List<Reservation> findByResourceIdAndStatus(Long resourceId, ReservationStatus status);
    
    // Buscar reservas activas de un recurso en un rango de fechas
    @Query("SELECT r FROM Reservation r WHERE r.resource = :resource " +
           "AND r.status = 'ACTIVE' " +
           "AND r.startDateTime < :endDateTime " +
           "AND r.endDateTime > :startDateTime")
    List<Reservation> findConflictingReservations(@Param("resource") Resource resource,
                                                   @Param("startDateTime") LocalDateTime startDateTime,
                                                   @Param("endDateTime") LocalDateTime endDateTime);
}