package com.capturenow.dto;

import com.capturenow.module.Packages;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;
@Data
public class PhotographerResponseDto {

    private String name;//required

    private String email;//required

//    private long phoneNumber;

    private String serviceLocation;//required

    private int experience;//required

    @Lob
    @Column(name = "profile_photo", columnDefinition="LONGBLOB")
    private byte[] profilePhoto;//required

    @JsonManagedReference
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.PERSIST)
    private List<Packages> Packages;

    private String services;//required

    private String languages;//required

    private String aboutMe;//required

    public void setEmail() {
    }
}
