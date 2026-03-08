package com.capturenow.controller;


import com.capturenow.config.ImageUtils;
import com.capturenow.dto.*;
import com.capturenow.module.Albums;
import com.capturenow.module.Packages;
import com.capturenow.module.Photographer;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.*;
import com.capturenow.serviceimpl.JwtService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Log4j2
@RestController
@Data
@RequestMapping(path = "/photographer")
public class PhotographerController {

	@Autowired
	private final PhotographerService service;

	@Autowired
	private final JwtService jwtservice;

	@Autowired
	private final PhotographerRepo repo;

	@Autowired
	private final PackageService packageService;

	@Autowired
	private final PhotographerCardDto photographerCardDto;

	@Autowired
	private final AlbumService albumService;

	@Autowired
	private final BookingService bookingService;

	@Autowired
	private final KycService kycService;

	@Qualifier("photographer")
	@Autowired
	private final AuthenticationManager authenticationManager;


	@PostMapping(path = "/signup")
	public ResponseEntity<Photographer> photographerSignUp(@RequestBody PhotographerRegistrationDTO p) {
		Photographer photographer = service.photographerSignup(p);
		if (photographer != null)
			return new ResponseEntity<Photographer>(photographer, HttpStatus.CREATED);
		return new ResponseEntity<Photographer>(photographer, HttpStatus.CONFLICT);
	}


	@GetMapping(path = "/Login")
	public ResponseEntity<PhotographerDTO> photographerLogin(@RequestParam String email, @RequestParam String password) {
		Photographer p = service.photographerSignin(email, password);
		PhotographerDTO dto = new PhotographerDTO();
		if (p != null) {
			dto.setId(p.getId());
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
			dto.setAvgRating(p.getAvgRating());
			if (p.getProfilePhoto() != null)
				dto.setProfilePhoto(ImageUtils.decompressImage(p.getProfilePhoto()));
			return new ResponseEntity<PhotographerDTO>(dto, HttpStatus.OK);
		}
		return new ResponseEntity<PhotographerDTO>(dto, HttpStatus.UNAUTHORIZED);
	}

	@GetMapping(path = "/validate")
	public ResponseEntity<?> validateEmail(@RequestParam String email, @RequestParam Integer otp) {
		Boolean b = service.validateEmail(email, otp);
		if (b) {
			return new ResponseEntity<>(b, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(b, HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/authtoken")
	public String authToken(@RequestParam String email, @RequestParam String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		log.info(authentication.isAuthenticated());
		if (authentication.isAuthenticated()) {
			String token = jwtservice.ganarateToken(email);
			Photographer p = repo.findByEmail(email);
			p.setAuthToken(token);
			repo.save(p);
			return token;
		}
		return "false";
	}

	@PostMapping(path = "/addAlbums")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Albums>> createAlbum(@RequestParam MultipartFile[] file,
													@RequestParam String category,
													@RequestParam String photographerName) {
		log.info("adding album");
		try {
			List<Albums> a = albumService.saveAlbum(file, category, photographerName);
			if (a != null) {
				return new ResponseEntity<List<Albums>>(a, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Albums>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/getAlbums")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<Page<Albums>> getAlbum(
			@RequestParam String email,
			@RequestParam int offset,
			@RequestParam int pageSize) {
		Page<Albums> albumsPage = albumService.downloadAlbum(email, offset, pageSize);
		return new ResponseEntity<>(albumsPage, HttpStatus.OK);
	}

	@PostMapping(path = "/addPackage")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Packages>> addPackage(@RequestBody PackageDto p) {
		List<Packages> pak = packageService.addPackage(p);
		if (pak != null)
			return new ResponseEntity<List<Packages>>(pak, HttpStatus.OK);
		return new ResponseEntity<List<Packages>>(pak, HttpStatus.FORBIDDEN);
	}

	@DeleteMapping(path = "/deletePackage")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> deletePackage(@RequestParam String id) {
		return new ResponseEntity<String>(packageService.deletePackage(id), HttpStatus.OK);
	}

	@GetMapping(path = "/getPackages")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<Packages>> getPackages(@RequestParam String email) {
		return new ResponseEntity<List<Packages>>(packageService.getAllPackages(email), HttpStatus.OK);
	}

	@GetMapping(path = "/getEquipment")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<Page<Albums>> getEquipments(@RequestParam String email,
													  @RequestParam int offset,
													  @RequestParam int pageSize) {
		return new ResponseEntity<>(albumService.downloadEquipments(email, offset, pageSize), HttpStatus.OK);
	}

	@DeleteMapping(path = "/deletePhoto")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> deletePhoto(@RequestParam String id) {
		return new ResponseEntity<String>(albumService.deleteAlbumById(id), HttpStatus.OK);
	}

	@PostMapping(path = "/changePhoto")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<byte[]> changePhoto(@RequestParam MultipartFile image, @RequestParam String email) {
		try {
			return new ResponseEntity<byte[]>(service.changeProfilePhoto(image, email), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@PutMapping(path = "/updateProfileInfo")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> updateInfo(@RequestBody PhotographerUpdateDto photographer) {
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

	@PostMapping(path = "/forgotPasswordOtp")
	public ResponseEntity<String> forgotPasswordOtp(@RequestParam String emailId) {
		return new ResponseEntity<String>(service.generateResetPasswordOtp(emailId), HttpStatus.OK);
	}

	@PostMapping(path = "/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String emailId, @RequestParam String newPassword, @RequestParam Integer otp) {
		return new ResponseEntity<String>(service.forgotPassword(emailId, newPassword, otp), HttpStatus.OK);
	}

	//	to fetch reviews from the customer
	@GetMapping(path = "/getReviews")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<RatingResponseDTO>> getReviews(@RequestParam String email) {
		List<RatingResponseDTO> reviews = service.getRatingsByEmail(email);
		return new ResponseEntity<List<RatingResponseDTO>>(reviews, HttpStatus.OK);
	}

	@GetMapping(path = "/getBookingStatus")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<List<BookingResponseStatusDto>> getBookingStatus(@RequestParam String emailId) {
		return new ResponseEntity<>(bookingService.getBookingStatusPhotographer(emailId), HttpStatus.OK);
	}

	@DeleteMapping(path = "/acceptDeclineBooking")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<Boolean> acceptDeclineBooking(@RequestParam Boolean status, @RequestParam String bookingId) {
		return new ResponseEntity<Boolean>(bookingService.acceptDeclineBooking(status, bookingId), HttpStatus.OK);
	}

	@PutMapping(path = "/addKycDetails")
	@PreAuthorize("hasAuthority('ROLE_PHOTOGRAPHER')")
	public ResponseEntity<String> addKycDetails(@RequestParam Long bankAccountNumber, @RequestParam String ifscCode, @RequestParam MultipartFile idProofImage, @RequestParam(required = false) MultipartFile studioLicence, @RequestParam String emailId) throws Exception {
		return new ResponseEntity<String>(kycService.addKycDetails(bankAccountNumber, ifscCode, idProofImage, studioLicence, emailId), HttpStatus.OK);
	}
}