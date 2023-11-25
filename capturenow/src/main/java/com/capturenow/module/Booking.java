package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Booking {

    @Id
    private String bookingId;

    private Date startDate;

    private Date endDate;

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

}
