package com.denise.cleaningservices.service;

import com.denise.cleaningservices.model.Booking;

import java.util.List;

public interface IBookingService {
    void cancelBooking(Long bookingId);

    Booking findBookingByConfirmationCode(String bookingConfirmationCode);

    void unassignWorkerFromBooking(Long bookingId, Long userId);

    List<Booking> getBookingsByUserEmail(String email);

    Booking createBooking(Long cleaningServiceId, Booking booking);

    Booking updateBookingStatus(Long bookingId);

    List<Booking> getAllBookings();

    Booking updateBookingStatusAndWorker(Long bookingId, Long workerId);

    List<Booking> getBookingsByCleaningServiceId(Long id);

    List<Booking> getWorkerBookingsByStatus(Long userId, String status);

    List<Booking> getBookingsByUserId(Long userId);

    Booking findById(Long bookingId);

    Long getBookingCountByUserId(Long workerId);
}
