package com.capturenow.dto;

import java.io.Serializable;

import lombok.Data;

@Data

public class PackageDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -962121062762564262L;

	private String email;

	private String packageName;

	private String category;

	private Double eventRate;

	private Double oneDayRate;

	private Double oneHourRate;

	private Double videoRate;

	private String description;

}
