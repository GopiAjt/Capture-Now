package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Booking {

    @Id
    @Column(name = "custom_id")
    private String bookingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;

    @OneToOne
    private Packages packages;

    @JsonBackReference
    @JoinColumn
    @ManyToOne
    private Photographer photographer;

    @JsonBackReference
    @JoinColumn
    @ManyToOne
    private Customer customer;

    public Booking(){
        this.bookingId = generateCustomId();
    }

    private String generateCustomId() {
        // Implement your custom ID generation logic here
        // Example: return UUID.randomUUID().toString();
        // You can use any logic to create a unique identifier
        return "CN" + UUID.randomUUID().toString();
    }

}
