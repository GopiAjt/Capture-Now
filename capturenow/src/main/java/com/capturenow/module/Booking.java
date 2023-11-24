package com.capturenow.module;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

}
