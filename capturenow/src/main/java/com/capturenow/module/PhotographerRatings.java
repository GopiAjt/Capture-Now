package com.capturenow.module;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PhotographerRatings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int ratings;
	
	private String comments;

	@JsonBackReference
	@JoinColumn
	@ManyToOne
	private Photographer photographer;

	@JsonBackReference
	@JoinColumn
	@ManyToOne
	private  Customer customer;
}
