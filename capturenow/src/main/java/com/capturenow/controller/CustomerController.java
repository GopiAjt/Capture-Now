package com.capturenow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.capturenow.module.Customer;
import com.capturenow.service.CustomerService;

@RestController
public class CustomerController {

	@Autowired
	CustomerService service;
	
	//localhost:8080/customerRegister
	@CrossOrigin
	@PostMapping("/customerReg")
	ResponseEntity<Customer> customerReg(@RequestBody Customer c)
	{
		return new ResponseEntity<Customer>(service.customerRegister(c), HttpStatus.CREATED);
	}
	
	//localhost:8080/customerLogin
	@CrossOrigin
	@GetMapping("/customerLogin")
	ResponseEntity<Customer> customerLogin(@RequestHeader String email,String password)
	{
		return new ResponseEntity<Customer>(service.customerLogin(email, password), HttpStatus.FOUND);
	}
}
