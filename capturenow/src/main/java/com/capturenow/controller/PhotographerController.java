package com.capturenow.controller;


import java.util.List;

import com.capturenow.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.capturenow.config.ImageUtils;
import com.capturenow.module.Albums;
import com.capturenow.module.Packages;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.PackageService;
import com.capturenow.service.PhotographerService;
import com.capturenow.serviceimpl.JwtService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@Data
@RequestMapping(path = "/photographer")
@CrossOrigin(origins = "http://127.0.0.1:5505")
public class PhotographerController {

	@Autowired
	private final PhotographerService service;

	@Autowired
	private final JwtService jwtservice;
	
	@Autowired
	private final PhotographerRepo repo;
	
	@Autowired
	private final PackageService packageService;

	@Qualifier("photographer")
	@Autowired
	private final AuthenticationManager authenticationManager;



	@PostMapping(path = "/signup")
	public ResponseEntity<Photographer> photographerSignUp(@RequestBody PhotographerRegistrationDTO p)
	{   
		Photographer photographer = service.photographerSignup(p);
		if(photographer != null)
			return new ResponseEntity<Photographer>(photographer, HttpStatus.CREATED);
		return new ResponseEntity<Photographer>(photographer, HttpStatus.CONFLICT);
	}
	
	
	@GetMapping(path = "/signin")
	public ResponseEntity<PhotographerDTO> photographerLogin(@RequestParam String email, @RequestParam String password)
	{
		Photographer p = service.photographerSignin(email, password);
		PhotographerDTO dto = new PhotographerDTO();
		if(p != null) {
			dto.setName(p.getName());
			dto.setEmail(p.getEmail());
			dto.setPhoneNumber(p.getPhoneNumber());
			dto.setProfilePhoto(p.getProfilePhoto());
			dto.setAboutMe(p.getAboutMe());
			dto.setExperience(p.getExperience());
			dto.setLanguages(p.getLanguages());
			dto.setServiceLocation(p.getServiceLocation());
			dto.setServices(p.getServices());
			dto.setAuthToken(p.getAuthToken());
			dto.setPackages(p.getPackages());
			if(p.getProfilePhoto() != null)
				dto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
			return new ResponseEntity<PhotographerDTO>(dto, HttpStatus.OK);
		}
		return new ResponseEntity<PhotographerDTO>(dto, HttpStatus.UNAUTHORIZED);
	}
	
	@GetMapping(path = "/validate")
	public ResponseEntity<?> validateEmail(@RequestParam String email, @RequestParam Integer otp)
	{
		Boolean b = service.validateEmail(email, otp);
		if(b)
		{
			return new ResponseEntity<>(b, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(b, HttpStatus.FORBIDDEN);
	}
	
	@GetMapping(path = "/authtoken")
	public String authToken(@RequestParam String email, @RequestParam String password)
	{
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		if(authentication.isAuthenticated())
		{
			String token = jwtservice.ganarateToken(email);
			Photographer p = repo.findByEmail(email);
			p.setAuthToken(token);
			repo.save(p);
			return token;			
		}
		return "false";
	}
	
	@PostMapping(path = "/add")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Albums>> createAlbum(@RequestParam MultipartFile[] file, 
	                                          @RequestParam String category, 
	                                          @RequestParam String photographerName) 
	{
		log.info("adding album");
	    try {
	    	List<Albums> a = service.saveAlbum(file, category, photographerName);
	        if(a!=null)
	        {
	        	return new ResponseEntity<List<Albums>>(a, HttpStatus.OK);
	        }
	        else {
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
	        }
	    	
	    } 
	    catch (Exception e){
	        return new ResponseEntity<List<Albums>>(HttpStatus.FORBIDDEN);
	    }
	}
	
	@GetMapping(path = "getAlbums")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Albums>> getAlbum(@RequestParam String email)
	{
		return new ResponseEntity<List<Albums>>(service.downlodeAlbum(email), HttpStatus.OK);
	}
	
	@PostMapping(path = "/addpackage")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Packages>> addPackage(@RequestBody PackageDto p)
	{
		List<Packages> pak = packageService.addPackage(p);
		if(pak != null)
			return new ResponseEntity<List<Packages>>(pak, HttpStatus.OK);
		return new ResponseEntity<List<Packages>>(pak, HttpStatus.FORBIDDEN);
	}
	
	@DeleteMapping(path = "/deletePackage")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> deletePackage(@RequestParam Integer id)
	{
		return new ResponseEntity<String>(packageService.deletePackage(id), HttpStatus.OK);
	}
	
	@GetMapping(path = "/getPackages")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Packages>> getPackages(@RequestParam String email)
	{
		return new ResponseEntity<List<Packages>>(packageService.getAllPackages(email), HttpStatus.OK);
	}
	
	@GetMapping(path = "/getEquipment")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Albums>> gerEquipments(@RequestParam String email)
	{
		return new ResponseEntity<List<Albums>>(service.downlodeEquipments(email), HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/deletePhoto")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> deletePhoto(@RequestParam Integer id)
	{
		return new ResponseEntity<String>(service.deletePhoto(id), HttpStatus.OK);
	}
	
	@PostMapping(path = "/changePhoto")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<byte[]> changePhoto(@RequestParam MultipartFile image, @RequestParam String email)
	{
		try {
			return new ResponseEntity<byte[]>(service.changeProfilePhoto(image, email), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@PutMapping(path = "/updateBasicInfo")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> updateInfo(@RequestBody PhotographerUpdateDto photographer)
	{
		return new ResponseEntity<String>(service.updateBasicInfo(photographer), HttpStatus.OK);
	}

	@PostMapping(path = "/resetPasswordOtp")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> generatePasswordResetOtp(@RequestParam String emailId) {
		return new ResponseEntity<String>(service.generateResetPasswordOtp(emailId), HttpStatus.OK);
	}

	@PostMapping(path = "/resetPassword")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
		return new ResponseEntity<String>(service.resetPassword(resetPasswordDto), HttpStatus.OK);
	}
}
