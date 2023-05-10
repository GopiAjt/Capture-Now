package com.capturenow.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capturenow.exceptions.InvalidCredentials;
import com.capturenow.module.Customer;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	CustomerRepo repo;
	
	@Override
	public Customer customerRegister(Customer c) {
		return repo.save(c);
	}

	@Override
	public Customer customerLogin(String email, String password) {
		List<Customer> c = repo.findByEmail(email);
		if(c.isEmpty())
		{
			Customer customer = c.get(0);
			if(customer.getCpassword().equals(password))
			{
				return customer;
			}
			else
			{
				throw new InvalidCredentials("Invalid Login Credentials");
			}
		}
		else
		{
			throw new InvalidCredentials("Invalid Login Credentials");
		}
	}

}
