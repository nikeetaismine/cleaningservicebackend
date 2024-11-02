package com.denise.cleaningservices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class CleaningService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private Double price;
    @Getter
    @Setter
    private String description;

    @Lob
    @JsonIgnore
    private Blob photo;

    @OneToMany(mappedBy = "cleaningService",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public CleaningService() {
        this.bookings = new ArrayList<>();
    }

}
