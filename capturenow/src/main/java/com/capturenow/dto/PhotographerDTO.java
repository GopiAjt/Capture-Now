package com.capturenow.dto;

import java.util.List;

import com.capturenow.module.Packages;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotographerDTO {

	private String name;//required

	private String email;//required

	private long phoneNumber;

	private String serviceLocation;//required

	private int experience;//required

	@Lob
	@Column(name = "profile_photo", columnDefinition="LONGBLOB")
	private byte[] profilePhoto;//required

	private String services;//required

	private String languages;//required

	private String aboutMe;//required

	@JsonManagedReference
	@OneToMany(mappedBy = "photographer", cascade = CascadeType.PERSIST)
	private List<Packages> Packages;

	private String authToken;



}