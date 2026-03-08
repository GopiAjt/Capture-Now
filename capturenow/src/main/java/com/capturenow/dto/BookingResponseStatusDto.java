package com.capturenow.dto;

import com.capturenow.module.Packages;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseStatusDto {

    private String bookingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String customerName;

    private String photographerName;

    private Packages bookedPackage;

    private String status;

    private LocalDateTime bookedDateTime;
}
