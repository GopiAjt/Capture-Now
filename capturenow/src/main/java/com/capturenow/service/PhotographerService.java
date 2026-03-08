package com.capturenow.service;

import java.util.ArrayList;
import java.util.List;

import com.capturenow.dto.*;
import com.capturenow.repository.PhotographerRepo;
import org.springframework.web.multipart.MultipartFile;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;

public interface PhotographerService {

	Photographer photographerSignup(PhotographerRegistrationDTO photographer);
	
	Photographer photographerSignin(String email, String password);
	
	Boolean validateEmail(String email, Integer otp);
	
	byte[] changeProfilePhoto(MultipartFile file, String email) throws Exception;
	
	String updateBasicInfo(PhotographerUpdateDto photographer);

	String generateResetPasswordOtp(String emailId);
	
	String resetPassword(ResetPasswordDto resetPasswordDto);

	List<RatingResponseDTO> getRatingsByEmail(String email);

	List<PhotographerCardDto> searchPhotographer(String query);

	String forgotPassword(String emailId, String newPassword, Integer otp);
}