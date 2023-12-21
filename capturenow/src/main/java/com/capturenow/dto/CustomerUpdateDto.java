package com.capturenow.dto;

import com.mysql.cj.exceptions.StreamingNotifiable;
import lombok.Data;

@Data
public class CustomerUpdateDto {

    private String name;

    private String email;

    private long phoneNo;

}
