package com.capturenow.dto;

import com.capturenow.module.Packages;
import com.capturenow.module.Photographer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PhotographerDTO {

	private String id;

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
	@OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL)
	private List<Packages> packages;

	private Double avgRating;

	private String authToken;

}