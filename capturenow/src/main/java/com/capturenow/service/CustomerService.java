package com.capturenow.service;

import org.springframework.stereotype.Service;

import com.capturenow.module.Customer;

public interface CustomerService {
	
	Customer customerRegister(Customer c);
	
	Customer customerLogin(String email, String password);

}
