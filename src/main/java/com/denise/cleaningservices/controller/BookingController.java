package com.denise.cleaningservices.controller;

import com.denise.cleaningservices.exception.InvalidBookingRequestException;
import com.denise.cleaningservices.exception.ResourceNotFoundException;
import com.denise.cleaningservices.model.Booking;
import com.denise.cleaningservices.model.CleaningService;
import com.denise.cleaningservices.model.User;
import com.denise.cleaningservices.response.BookingResponse;
import com.denise.cleaningservices.response.CleaningServiceResponse;
import com.denise.cleaningservices.service.IBookingService;
import com.denise.cleaningservices.service.ICleaningServiceService;
import com.denise.cleaningservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;
    private final ICleaningServiceService cleaningServiceService;
    private final UserService userService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            System.out.println("BookingResponse: " + bookingResponse);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/cleaning-service/{id}/booking")
    public ResponseEntity<?> createBooking(@PathVariable Long id, @RequestBody Booking booking) {
        try {
            Booking savedBooking = bookingService.createBooking(id, booking);
            String bookingConfirmationCode = savedBooking.getBookingConfirmationCode();
            return ResponseEntity.ok(bookingConfirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping ("/{bookingId}/assign")
    public ResponseEntity<Booking> updateBookingStatusAndWorker(@PathVariable Long bookingId, @RequestParam Long workerId) {
        Booking updatedBooking = bookingService.updateBookingStatusAndWorker(bookingId, workerId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long bookingId) {
        Booking updatedBooking = bookingService.updateBookingStatus(bookingId);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<Booking> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/worker/bookings")
    public ResponseEntity<List<BookingResponse>> getWorkerBookings(@RequestParam Long userId) {
        List<Booking> workerBookings = bookingService.getBookingsByUserId(userId);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : workerBookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }


    @PostMapping("/unassign/{bookingId}/{userId}")
    public ResponseEntity<Void> unassignWorkerFromBooking(
            @PathVariable Long bookingId,
            @PathVariable Long userId) {
        bookingService.unassignWorkerFromBooking(bookingId, userId);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/confirmation/{bookingConfirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String bookingConfirmationCode) {
        try {
            Booking booking = bookingService.findBookingByConfirmationCode(bookingConfirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}/scheduled")
    public ResponseEntity<List<Booking>> getWorkerScheduledBookings(@PathVariable Long workerId) {
        List<Booking> bookings = bookingService.getWorkerBookingsByStatus(workerId, "scheduled");
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/worker/{workerId}/completed")
    public ResponseEntity<List<Booking>> getWorkerCompletedBookings(@PathVariable Long workerId) {
        List<Booking> bookings = bookingService.getWorkerBookingsByStatus(workerId, "completed");
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/worker/{workerId}/in-progress")
    public ResponseEntity<List<Booking>> getWorkerInProgressBookings(@PathVariable Long workerId) {
        List<Booking> bookings = bookingService.getWorkerBookingsByStatus(workerId, "in progress");
        return ResponseEntity.ok(bookings);
    }


    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    @GetMapping("/worker/{workerId}/booking-count")
    public ResponseEntity<Long> getWorkerBookingCount(@PathVariable Long workerId) {
        Long count = bookingService.getBookingCountByUserId(workerId);
        return ResponseEntity.ok(count);
    }

    private BookingResponse getBookingResponse(Booking booking) {
        CleaningService cleaningService = cleaningServiceService.getCleaningServiceById(booking.getCleaningService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cleaning Service not found"));

        CleaningServiceResponse cleaningServiceResponse = new CleaningServiceResponse(
                cleaningService.getId(),
                cleaningService.getName(),
                cleaningService.getDescription(),
                cleaningService.getPrice());
        return new BookingResponse(
                        booking.getBookingId(),
                        booking.getStartDate(),
                        booking.getEndDate(),
                        booking.getClientFullName(),
                        booking.getClientEmail(),
                        booking.getStatus(),
                        booking.getBookingConfirmationCode(),
                        cleaningServiceResponse.getName());
    }

    @GetMapping("/worker-fullname/{bookingId}")
    public ResponseEntity<String> getWorkerFullNameByBookingId(@PathVariable Long bookingId) {
        Booking booking = bookingService.findById(bookingId);
        if (booking.getUser() != null) {
            User worker = booking.getUser();
            String fullName = worker.getFirstName() + " " + worker.getLastName();
            return ResponseEntity.ok(fullName);
        }
        return ResponseEntity.ok("Unassigned");
    }
    
}
