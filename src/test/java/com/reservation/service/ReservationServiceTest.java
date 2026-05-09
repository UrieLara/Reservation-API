package com.reservation.service;

import com.reservation.model.entity.Reservation;
import com.reservation.model.entity.Resource;
import com.reservation.model.entity.User;
import com.reservation.model.enums.ReservationStatus;
import com.reservation.model.enums.Role;
import com.reservation.repository.ReservationRepository;
import com.reservation.repository.ResourceRepository;
import com.reservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;
    private Resource testResource;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(Role.USER);

        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Sala Test");
        testResource.setIsActive(true);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUser(testUser);
        testReservation.setResource(testResource);
        testReservation.setStartDateTime(LocalDateTime.now().plusDays(1));
        testReservation.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
        testReservation.setStatus(ReservationStatus.ACTIVE);
    }

    @Test
    void cancelReservation_ShouldCancel_WhenUserIsOwner() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When
        reservationService.cancelReservation(1L, "test@example.com", "Ya no la necesito");

        // Then
        assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(testReservation.getCancellationReason()).isEqualTo("Ya no la necesito");
        verify(reservationRepository).save(testReservation);
    }

    @Test
    void cancelReservation_ShouldThrow_WhenReservationNotFound() {
        // Given
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(99L, "test@example.com", "Razón"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reserva no encontrada");
    }

    @Test
    void cancelReservation_ShouldThrow_WhenReservationAlreadyCancelled() {
        // Given
        testReservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(1L, "test@example.com", "Razón"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Solo se pueden cancelar reservas activas");
    }

    @Test
    void cancelReservation_ShouldThrow_WhenLessThan2HoursBeforeStart() {
        // Given
        testReservation.setStartDateTime(LocalDateTime.now().plusMinutes(30));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(1L, "test@example.com", "Razón"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("menos de 2 horas de anticipación");
    }
}