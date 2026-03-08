package com.capturenow.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDto {

    private String name;

    private String email;

    private long phoneNo;

    @Lob
    @Column(name = "profile_photo", columnDefinition="LONGBLOB")
    private byte[] profilePhoto;

    private List<String> favorites;

    private String authToken;
}
