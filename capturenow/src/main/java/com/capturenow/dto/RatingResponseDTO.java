package com.capturenow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponseDTO {

    private  String ratingId;

    private int rating;

    private String comment;

    private String customerName;

    private String customerId;

    private LocalDateTime ratingDate;

    private byte[] customerProfilePhoto;
}
