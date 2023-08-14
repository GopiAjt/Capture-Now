package com.capturenow.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PhotographerRepo;

@Component
public class PhotographerUserDetailsService implements UserDetailsService{

	@Autowired
	private PhotographerRepo photographerRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Photographer p = photographerRepo.findByEmail(username);
		if(p != null)
		{
			return p;
		}else
		{
			throw new UsernameNotFoundException("Not a Valid User");
		}
	}  
}