package com.capturenow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhotographerCardDto {

	private String name;

	private String mailId;
	
	private String serviceLocation;
	
	private int experience;//required
	
	private String services;//required
	
	private String languages;
	
	private byte[] profilePhoto;
}
