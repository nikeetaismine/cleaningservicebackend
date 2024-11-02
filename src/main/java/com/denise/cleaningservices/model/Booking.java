package com.denise.cleaningservices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;


    @Column(name = "client_full_name")
    private String clientFullName;

    @Column(name = "client_email")
    private String clientEmail;

    @Getter
    @Setter
    private String status; // Possible values: "pending scheduling", "scheduled", "in progress", "completed"
    @Setter
    @Getter
    private String bookingConfirmationCode;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cleaning_service_id")
    private CleaningService cleaningService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
