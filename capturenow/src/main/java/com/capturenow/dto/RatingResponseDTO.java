package com.capturenow.dto;

import lombok.Data;

@Data
public class RatingResponseDTO {

    private int rating;

    private String comment;

    private String customerName;

    private byte[] customerProfilePhoto;
}
