package com.capturenow.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String packageId;

    private String customerId;

    private String photographerId;

    private LocalDateTime bookedDateTime;
}
