package com.agendai.repository;

import com.agendai.model.Booking;
import com.agendai.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByProfessionalId(Long professionalId);
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    Page<Booking> findByProfessionalId(Long professionalId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.professional.id = :professionalId " +
            "AND b.bookingDateTime BETWEEN :start AND :end " +
            "AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Booking> findConflictingBookings(
            @Param("professionalId") Long professionalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.bookingDateTime BETWEEN :start AND :end " +
            "AND b.status = 'CONFIRMED'")
    List<Booking> findUpcomingBookings(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
            "AND b.status = :status ORDER BY b.bookingDateTime DESC")
    List<Booking> findByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.professional.id = :professionalId " +
            "AND b.bookingDateTime BETWEEN :start AND :end")
    long countByProfessionalAndDateRange(
            @Param("professionalId") Long professionalId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}