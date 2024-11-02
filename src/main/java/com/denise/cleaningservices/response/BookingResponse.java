package com.denise.cleaningservices.response;

import com.denise.cleaningservices.model.User;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String clientFullName;
    private String clientEmail;
    private String status;
    private String bookingConfirmationCode;
    private String cleaningServiceName;
    private String workerName;


    public BookingResponse(Long bookingId, LocalDate startDate, LocalDate endDate, String clientFullName, String clientEmail, String status, String bookingConfirmationCode, String name) {
        this.bookingId = bookingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clientFullName = clientFullName;
        this.clientEmail = clientEmail;
        this.status = status;
        this.bookingConfirmationCode = bookingConfirmationCode;
        this.cleaningServiceName = name;
    }

    public BookingResponse(String workerName){
        this.workerName = workerName;
    }
}
