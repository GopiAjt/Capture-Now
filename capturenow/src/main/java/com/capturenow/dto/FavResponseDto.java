package com.capturenow.dto;

import lombok.Data;

@Data
public class FavResponseDto {

    private String name;

    private String pId;

    private byte[] profilePhoto;

    private Double avgRating;

    private Double startsWith;
}
