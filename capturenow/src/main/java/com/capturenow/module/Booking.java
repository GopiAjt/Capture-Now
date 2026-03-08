package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@Data
public class Booking {

    @Id
    @Column(name = "book_id")
    private String bookingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    private LocalDateTime bookedDateTime;

    @OneToOne
    private Packages packages;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photographer_id")
    private Photographer photographer;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Booking(){

        this.bookingId = generateCustomId();
    }

    private String generateCustomId() {
        Random r = new Random();

        // Implement your custom ID generation logic here
        // Example: return UUID.randomUUID().toString();
        // You can use any logic to create a unique identifier
        return "CN-B" + r.nextInt(100000, 999999);
    }

}
