package com.capturenow.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String emailId;

    private String oldPassword;

    private String newPassword;

    private Integer otp;
}
