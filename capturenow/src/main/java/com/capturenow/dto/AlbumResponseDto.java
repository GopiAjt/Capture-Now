package com.capturenow.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class AlbumResponseDto {

    private byte[] photo;

    private String category;

    private String name;
}
