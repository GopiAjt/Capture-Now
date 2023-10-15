package com.capturenow.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class AlbumResponseDto {

    @Lob
    @Column(name = "photo", columnDefinition="LONGBLOB")
    private byte[] photo;

}
