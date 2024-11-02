package com.denise.cleaningservices.repository;

import com.denise.cleaningservices.model.Booking;
import com.denise.cleaningservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.bookingId = :bookingId")
    Optional<Booking> findByBookingId(@Param("bookingId") Long bookingId);

    Optional<Booking> findBookingByBookingConfirmationCode(String bookingConfirmationCode);

    List<Booking> findByCleaningServiceId(Long cleaningServiceId);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdAndStatus(Long userId, String status);

    List<Booking> findByUserEmail(String email);

    @Query("SELECT b FROM Booking b WHERE b.clientEmail = :clientEmail")
    List<Booking> findByClientEmail(@Param("clientEmail") String clientEmail);

    Long countByUserId(Long userId);
}
