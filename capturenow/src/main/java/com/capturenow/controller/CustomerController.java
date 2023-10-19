package com.capturenow.controller;

import java.util.List;

import com.capturenow.dto.AlbumResponseDto;
import com.capturenow.dto.PhotographerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capturenow.dto.PhotographerCardDto;
import com.capturenow.module.Customer;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.service.CustomerService;
import com.capturenow.serviceimpl.JwtService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@Data
@RequestMapping(path = "/customer")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class CustomerController {

	private final CustomerService service;

	private final JwtService jwtservice;

	private final CustomerRepo customerRepo;
	
	@Qualifier("customer")
	@Autowired
	private final AuthenticationManager authenticationManager;

	//localhost:8080/customer/signup
	@PostMapping(path = "/signup")
	ResponseEntity<Customer> customerSignUp(@RequestBody Customer c)
	{
		Customer customer = service.customerRegister(c);
		if(customer!= null)
			return new ResponseEntity<Customer>(customer, HttpStatus.CREATED);
		return new ResponseEntity<Customer>(customer,HttpStatus.CONFLICT);
	}

	@GetMapping(path = "/validate")
//	@PreAuthorize("hasAuthority('ROLE_USER')")
	ResponseEntity<?> validateEmail(@RequestParam String email, @RequestParam Integer otp)
	{
		Boolean b = service.validateEmail(email, otp);
		if(b)
		{
			return new ResponseEntity<>(b, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(b, HttpStatus.FORBIDDEN);
	}


	//localhost:8080/customer/sign_in
	@GetMapping(path = "/signin")
	ResponseEntity<Customer> customerLogin(@RequestParam String email, @RequestParam String password)
	{
		Customer c = service.customerLogin(email, password);
		if(c != null)
			return new ResponseEntity<Customer>(c, HttpStatus.OK);
		return new ResponseEntity<Customer>(c, HttpStatus.UNAUTHORIZED);
	}

	@GetMapping(path = "/authtoken")
	public String authAndGetToken(@RequestParam String email, @RequestParam String password)
	{
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		log.info(authentication.isAuthenticated());
		if(authentication.isAuthenticated())
		{
			String token = jwtservice.ganarateToken(email);
			Customer c = customerRepo.findByEmail(email);
			c.setAuthToken(token);
			customerRepo.save(c);
			return token;
		}
		else
		{
			throw new UsernameNotFoundException("invalid user request !");
		}
	}
	
	@GetMapping(path = "/getPhotographers")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<List<PhotographerCardDto>> getAllPhotographers()
	{
		return new ResponseEntity<List<PhotographerCardDto>>(service.getAllPhotographers(), HttpStatus.OK);
	}
	
	@GetMapping(path = "/getPhotographerByEmail")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<PhotographerResponseDto> getPhotographerByEmail(@RequestParam String email)
	{
		return new ResponseEntity<PhotographerResponseDto>(service.getPhotographerById(email), HttpStatus.OK);
	}

	@GetMapping(path = "/getAlbumsByEmail")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<List<AlbumResponseDto>> getAlbumByEmail(@RequestParam String email)
	{
		return new ResponseEntity<List<AlbumResponseDto>>(service.getAlbumByEmail(email), HttpStatus.OK);
	}

	@GetMapping(path = "/getEquipmentsByEmail")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<List<AlbumResponseDto>> getEquipmentsByEmail(@RequestParam String email)
	{
		return new ResponseEntity<List<AlbumResponseDto>>(service.getEquipmentsByEmail(email), HttpStatus.OK);
	}

}
