package com.web.captureNow.service;

import com.web.captureNow.module.Customer;

public interface CustomerService {

	Customer customerRegister(Customer c);
	
	Customer customerLog(String email, String password);
}
