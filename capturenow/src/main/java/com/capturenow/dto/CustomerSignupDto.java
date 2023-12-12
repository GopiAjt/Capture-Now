package com.capturenow.dto;

import lombok.Data;

@Data
public class CustomerSignupDto {

    private String name;

    private String email;

    private long phoneNo;

    private String password;
}
