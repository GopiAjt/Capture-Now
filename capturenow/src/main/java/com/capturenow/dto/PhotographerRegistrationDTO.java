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

    private String serviceLocation;//required

    private int experience;//required

    private String services;//required

    private String languages;//required

    @Column(length = 500)
    private String aboutMe;//required

}
