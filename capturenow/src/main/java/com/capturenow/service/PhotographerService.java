package com.capturenow.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.capturenow.dto.PhotographerUpdateDto;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;

public interface PhotographerService {

	Photographer photographerSignup(Photographer photographer);
	
	Photographer photographerSignin(String email, String password);
	
	Boolean validateEmail(String email, Integer otp);
	
	List<Albums> saveAlbum(MultipartFile[] file, String category, String photographerName) throws Exception;
	
	List<Albums> downlodeAlbum(String email);
	
	List<Albums> downlodeEquipments(String email);
	
	String deletePhoto(int id);
	
	byte[] changeProfilePhoto(MultipartFile file, String email) throws Exception;
	
	String updateBasicInfo(PhotographerUpdateDto photographer);
	
	String resetPassword(String email, String password);
}