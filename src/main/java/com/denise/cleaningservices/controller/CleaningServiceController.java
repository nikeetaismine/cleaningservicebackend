package com.denise.cleaningservices.controller;

import com.denise.cleaningservices.exception.PhotoRetrievalException;
import com.denise.cleaningservices.exception.ResourceNotFoundException;
import com.denise.cleaningservices.model.Booking;
import com.denise.cleaningservices.model.CleaningService;
import com.denise.cleaningservices.response.BookingResponse;
import com.denise.cleaningservices.response.CleaningServiceResponse;
import com.denise.cleaningservices.service.BookingService;
import com.denise.cleaningservices.service.ICleaningServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cleaning_services")
@RequiredArgsConstructor
public class CleaningServiceController {

    private final ICleaningServiceService cleaningServiceService;
    private final BookingService bookingService;


    @PostMapping("/add/new-cleaning-service")

    public ResponseEntity<CleaningServiceResponse> addNewCleaningService (
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("photo") MultipartFile photo) throws IOException, SQLException {
        CleaningService savedCleaningService = cleaningServiceService.addNewCleaningService(name,description,price,photo);
        CleaningServiceResponse response = new CleaningServiceResponse(
                savedCleaningService.getId(),
                savedCleaningService.getName(),
                savedCleaningService.getDescription(),
                savedCleaningService.getPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-cleaning-services")
    public ResponseEntity<List<CleaningServiceResponse>> getAllCleaningServices() throws SQLException {
        List<CleaningService> cleaningServices = cleaningServiceService.getAllCleaningServices();
        List<CleaningServiceResponse> cleaningServiceResponses = new ArrayList<>();
        for(CleaningService cleaningService : cleaningServices){
            byte[] photoBytes = cleaningServiceService.getCleaningServicePhotoById(cleaningService.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
                CleaningServiceResponse cleaningServiceResponse = getCleaningServiceResponse(cleaningService);
                cleaningServiceResponse.setPhoto(base64Photo);
                cleaningServiceResponses.add(cleaningServiceResponse);
            }

        }
        return ResponseEntity.ok(cleaningServiceResponses);
    }

    @DeleteMapping("/delete/cleaning-service/{id}")
    public ResponseEntity<Void> deleteCleaningService(@PathVariable Long id) throws SQLException {
        cleaningServiceService.deleteCleaningService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CleaningServiceResponse> updateCleaningService(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {

        byte[] photoBytes = null;
        if (photo != null && !photo.isEmpty()) {
            photoBytes = photo.getBytes();
        } else {
            photoBytes = cleaningServiceService.getCleaningServicePhotoById(id);
        }

        Blob photoBlob = (photoBytes != null && photoBytes.length > 0) ? new SerialBlob(photoBytes) : null;
        CleaningService theCleaningService = cleaningServiceService.updateCleaningService(id, name, description, price, photoBytes);
        theCleaningService.setPhoto(photoBlob);

        CleaningServiceResponse cleaningServiceResponse = getCleaningServiceResponse(theCleaningService);
        return ResponseEntity.ok(cleaningServiceResponse);
    }


    @GetMapping("/cleaningService/{id}")
    public ResponseEntity<Optional<CleaningServiceResponse>> getCleaningServiceById(@PathVariable Long id){
        Optional<CleaningService> theCleaningService = cleaningServiceService.getCleaningServiceById(id);
        return theCleaningService.map(cleaningService -> {
            CleaningServiceResponse cleaningServiceResponse = getCleaningServiceResponse(cleaningService);
            return  ResponseEntity.ok(Optional.of(cleaningServiceResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    @GetMapping("/available-cleaning-services")
    public ResponseEntity<List<CleaningServiceResponse>> getAvailableCleaningServices(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
            @RequestParam("name") String name) throws SQLException {
        List<CleaningService> availableCleaningServices = cleaningServiceService.getAvailableCleaningServices(startDate, endDate, name);
        List<CleaningServiceResponse> cleaningServiceResponses = new ArrayList<>();
        for (CleaningService cleaningService : availableCleaningServices){
            byte[] photoBytes = cleaningServiceService.getCleaningServicePhotoById(cleaningService.getId());
            if (photoBytes != null && photoBytes.length > 0){
                String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                CleaningServiceResponse cleaningServiceResponse = getCleaningServiceResponse(cleaningService);
                cleaningServiceResponse.setPhoto(photoBase64);
                cleaningServiceResponses.add(cleaningServiceResponse);
            }
        }
        if(cleaningServiceResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(cleaningServiceResponses);
        }
    }

    private CleaningServiceResponse getCleaningServiceResponse(CleaningService cleaningService) {
        List<Booking> bookings = getAllBookingsByCleaningServiceId(cleaningService.getId());

        List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(
                        booking.getBookingId(),
                        booking.getStartDate(),
                        booking.getEndDate(),
                        booking.getClientFullName(),
                        booking.getClientEmail(),
                        booking.getStatus(),
                        booking.getBookingConfirmationCode(),
                        cleaningService.getName())).toList();
        byte [] photoBytes = null;
        Blob photoBlob = cleaningService.getPhoto();
        if(photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1,(int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new CleaningServiceResponse(cleaningService.getId(),
                cleaningService.getName(),
                cleaningService.getDescription(),
                cleaningService.getPrice(), photoBytes, bookingInfo);
    }

    private List<Booking> getAllBookingsByCleaningServiceId(Long id) {
        return bookingService.getBookingsByCleaningServiceId(id);
    }

}
