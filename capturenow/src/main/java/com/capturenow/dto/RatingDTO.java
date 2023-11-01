package com.capturenow.dto;

import lombok.Data;

@Data
public class RatingDTO {

    private String customerId;

    private String photographerId;

    private int rating;

    private String comment;

}
