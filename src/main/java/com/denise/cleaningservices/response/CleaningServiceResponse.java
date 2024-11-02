package com.denise.cleaningservices.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CleaningServiceResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String photo;
    private List<BookingResponse> bookings = new ArrayList<>();

    public CleaningServiceResponse(Long id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public CleaningServiceResponse(Long id, String name, String description, Double price, byte[] photoBytes, List<BookingResponse> bookingInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.photo = photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;
        this.bookings = bookingInfo;
    }
}
