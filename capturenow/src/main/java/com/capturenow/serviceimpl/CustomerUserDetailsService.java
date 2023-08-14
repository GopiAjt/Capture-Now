package com.capturenow.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.capturenow.module.Customer;
import com.capturenow.repository.CustomerRepo;

@Component
public class CustomerUserDetailsService implements UserDetailsService{

	@Autowired
	private CustomerRepo customerRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer c = customerRepo.findByEmail(username);
		if(c != null)
		{
			return c;
		}else
		{
			throw new UsernameNotFoundException("Not a Valid User");
		}
	}

}
