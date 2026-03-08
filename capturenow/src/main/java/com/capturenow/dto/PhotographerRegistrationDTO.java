package com.capturenow.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.Date;

@Data
public class PhotographerRegistrationDTO {
    @Column(nullable = false)
    private String name;//required

    @Column(nullable = false)
    private String email;//required

    @Column(nullable = false)
    private String password;//required

    private long phoneNumber;

}
