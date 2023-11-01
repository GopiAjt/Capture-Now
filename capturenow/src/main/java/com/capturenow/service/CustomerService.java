package com.capturenow.service;

import java.util.List;

import com.capturenow.dto.*;
import com.capturenow.module.Customer;

public interface CustomerService {
	
	Customer customerRegister(Customer c);
	
	Customer customerLogin(String email, String password);

	Boolean validateEmail(String email, Integer otp);

	List<PhotographerCardDto> getAllPhotographers();

	PhotographerResponseDto getPhotographerById(String email);

	List<AlbumResponseDto> getAlbumByEmail(String email);

	List<AlbumResponseDto> getEquipmentsByEmail(String email);

	boolean addReview(RatingDTO ratingDTO);

	RatingResponseDTO getRatingsByEmail(String email);
}
