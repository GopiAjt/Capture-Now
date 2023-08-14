package com.capturenow.dto;

import lombok.Data;

@Data
public class PhotographerUpdateDto {
	

	private String name;//required

	private String email;//required

	private long phoneNumber;

	private String serviceLocation;//required

	private int experience;//required

	private String services;//required

	private String languages;//required

	private String aboutMe;//required

	private String authToken;

}
