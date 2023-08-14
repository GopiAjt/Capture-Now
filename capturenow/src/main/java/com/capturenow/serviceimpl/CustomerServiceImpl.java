package com.capturenow.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.capturenow.config.ImageUtils;
import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.dto.PhotographerDTO;
import com.capturenow.email.EmailService;
import com.capturenow.module.Customer;
import com.capturenow.module.Photographer;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.CustomerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Service
@Data
public class CustomerServiceImpl implements CustomerService{

	private final CustomerRepo repo;//login to talk with the database
	
	private final PhotographerRepo photographerRepo;
	
	private final EmailService emailService;//logic to send the email
	
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();//to encode and decode the password
	
	@Override
	public Customer customerRegister(Customer c) {
		Customer exist = repo.findByEmail(c.getEmail());//check if the user with email allready exists
		
		if(exist == null)
		{
			//emailService.sendToCustomer(c.getEmail(),c);//send email to the user with otp
			
			c.setPassword(encoder.encode(c.getPassword()));//encode the password with BCrypt
			c.setSignupDateTime(new Date());//set the data time when the account is created
			c.setStatus(false);//set otp verification status as false
			c.setLogin(false);//set login status as false
			return repo.save(c);//save the data into the data base
		}
		else
		{
			return null;
		}
		
		
	}

	@Override
	public Customer customerLogin(String email, String password) {
		Customer c = repo.findByEmail(email);//find the user with the email provided
		if(c != null)//check if the user with email is present
		{
			if(c.isStatus())//check if the otp is verified or not
			{
				if(encoder.matches(password, c.getPassword()))//check password matches or not
				{
					c.setLogin(true);
					return repo.save(c);
				}
				else
				{
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Invalid Password");
				}
			}
			else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Please Verify Your Account");
			}
		}
		else
		{
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not a Valid Email");
		}
	}

	@Override
	public Boolean validateEmail(String email, Integer otp) {
		
		Customer c = repo.findByEmail(email);//find the user with the provided email
		if(c != null)
		{
			if(c.getSignupVerificationKey() == otp)//check the otp present in the database is equal to otp provided by the user
			{
				c.setStatus(true);//set the otp status as true
				c.setSignupVerificationKey(0);
				repo.save(c);//update the details to the database
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not a Valid User");
		}
	}

	@Override
	public List<PhotographerCardDto> getAllPhotographers() {
		List<Photographer> p = photographerRepo.findAll();
		if(p == null)
		{
			return null;
		}
		List<PhotographerCardDto> card = new ArrayList<>(p.size());
		for(Photographer photographer : p)
		{
			PhotographerCardDto cardDto = new PhotographerCardDto(photographer.getName(),photographer.getServiceLocation(),photographer.getExperience(), photographer.getServices(),photographer.getLanguages(),photographer.getProfilePhoto());
			if(cardDto.getProfilePhoto() != null)
			{
				cardDto.setProfilePhoto(ImageUtils.decompressImage(cardDto.getProfilePhoto()));
			}
			card.add(cardDto);
		}
		return card;
	}

	@Override
	public PhotographerDTO getPhotographerById(String email) {
		Photographer photographer = photographerRepo.findByEmail(email);
		if(photographer != null)
		{
			PhotographerDTO p = new PhotographerDTO();
			p.setName(photographer.getName());
			p.setPhoneNumber(photographer.getPhoneNumber());
			p.setExperience(photographer.getExperience());
			p.setLanguages(photographer.getLanguages());
			p.setAboutMe(photographer.getAboutMe());
			p.setServices(photographer.getServices());
			p.setServiceLocation(photographer.getServiceLocation());
			p.setPackages(photographer.getPackages());
			p.setProfilePhoto(ImageUtils.decompressImage(photographer.getProfilePhoto()));
			
			return p;
		}
		return null;
	}

}
