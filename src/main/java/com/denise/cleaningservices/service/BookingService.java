package com.denise.cleaningservices.service;

import com.denise.cleaningservices.exception.ResourceNotFoundException;
import com.denise.cleaningservices.model.Booking;
import com.denise.cleaningservices.model.CleaningService;
import com.denise.cleaningservices.model.User;
import com.denise.cleaningservices.repository.BookingRepository;
import com.denise.cleaningservices.repository.CleaningServiceRepository;
import com.denise.cleaningservices.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final CleaningServiceRepository cleaningServiceRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, CleaningServiceRepository cleaningServiceRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.cleaningServiceRepository = cleaningServiceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println("Bookings fetched from database: " + bookings);
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByCleaningServiceId(Long cleaningServiceId) {
        return bookingRepository.findByCleaningServiceId(cleaningServiceId);
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        bookingRepository.delete(booking);
    }

    @Override
    public Booking findBookingByConfirmationCode(String bookingConfirmationCode) {
        return bookingRepository.findBookingByBookingConfirmationCode(bookingConfirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Override
    public List<Booking> getBookingsByUserEmail(String email) {

        return bookingRepository.findByClientEmail(email);
    }

    @Override
    public Booking createBooking(Long cleaningServiceId, Booking booking) {
        String confirmationCode = generateConfirmationCode();
        booking.setBookingConfirmationCode(confirmationCode);
        CleaningService cleaningService = cleaningServiceRepository.findById(cleaningServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning Service not found"));
        booking.setCleaningService(cleaningService);
        booking.setStatus("pending scheduling");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID: " + bookingId));

        if ("scheduled".equalsIgnoreCase(booking.getStatus())) {
            if (LocalDate.now().isAfter(booking.getStartDate()) || LocalDate.now().isEqual(booking.getStartDate())) {
                booking.setStatus("in progress");
            } else {
                booking.setStatus("scheduled");
            }
        } else if ("in progress".equalsIgnoreCase(booking.getStatus())) {
            booking.setStatus("completed");
        } else if ("completed".equalsIgnoreCase(booking.getStatus())) {
            booking.setStatus("completed");
        }
        else {
            booking.setStatus("scheduled");
        }

        return bookingRepository.save(booking);
    }

    private String generateConfirmationCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Booking updateBookingStatusAndWorker(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID: " + bookingId));



        booking.setStatus("scheduled");

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
            booking.setUser(user);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Transactional
    @Override
    public void unassignWorkerFromBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        User worker = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found"));

        // Ensure that the user is indeed a worker
        if (!worker.getRoles().stream().anyMatch(role -> role.getId() == 3L)) {
            throw new RuntimeException("User is not a worker");
        }

        // Check if the booking is assigned to the worker
        if (booking.getUser() != null && booking.getUser().getId().equals(worker.getId())) {
            booking.setUser(null);
            bookingRepository.save(booking);
        } else {
            throw new RuntimeException("The worker is not assigned to this booking");
        }
    }

    @Override
    public List<Booking> getWorkerBookingsByStatus(Long userId, String status) {
        return bookingRepository.findByUserIdAndStatus(userId, status);
    }

    public Long getBookingCountByUserId(Long userId) {
        return bookingRepository.countByUserId(userId);
    }

}