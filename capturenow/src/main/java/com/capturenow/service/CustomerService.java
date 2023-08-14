package com.capturenow.service;

import java.util.List;

import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.dto.PhotographerDTO;
import com.capturenow.module.Customer;

public interface CustomerService {
	
	Customer customerRegister(Customer c);
	
	Customer customerLogin(String email, String password);

	Boolean validateEmail(String email, Integer otp);

	List<PhotographerCardDto> getAllPhotographers();
	
	PhotographerDTO getPhotographerById(String email);
	
}
