package com.web.captureNow.serviceimpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.captureNow.exceptions.InvalidCredentials;
import com.web.captureNow.module.Customer;
import com.web.captureNow.repository.CustomerRepo;
import com.web.captureNow.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepo repo;
	
	BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
	
	@Override
	public Customer customerRegister(Customer c) {
		c.setCpassword(bc.encode(c.getCpassword()));
		return repo.save(c);
	}

	@Override
	public Customer customerLog(String email, String password) {
		Customer c = repo.validate(email);
		if(c != null)
		{
			if(bc.matches(password, c.getCpassword()))
			{
				return c;
			}
			else
			{
				throw new InvalidCredentials("Invalid login Credentials");
			}
		}
		else
		{
			throw new InvalidCredentials("Invalid login Credentials");
		}

	}

	
}
