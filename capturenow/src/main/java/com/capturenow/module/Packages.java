package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Packages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String packageName;
	
	private String category;

	private int eventRate;
	
	private int oneDayRate;
	
	private int oneHourRate;
	
	private int videoRate;
	

    @Column(length = 4000)
	private String description;

	@JsonBackReference
	@JoinColumn
	@ManyToOne
	private Photographer photographer;
	
}
