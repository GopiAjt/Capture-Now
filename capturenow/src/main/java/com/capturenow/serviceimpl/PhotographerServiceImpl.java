package com.capturenow.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.capturenow.dto.PhotographerRegistrationDTO;
import com.capturenow.dto.ResetPasswordDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.capturenow.config.ImageUtils;
import com.capturenow.dto.PhotographerUpdateDto;
import com.capturenow.email.EmailService;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;
import com.capturenow.repository.AlbumRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.PhotographerService;
import lombok.Data;


@Service
@Data
public class PhotographerServiceImpl implements PhotographerService{

	private final PhotographerRepo repo;

	private final EmailService emailService;//logic to send the email

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	private final StorageService storageService;

	private final AlbumRepo albumRepo;

	@Override
	public Photographer photographerSignup(PhotographerRegistrationDTO photographer) {

		Photographer exist = repo.findByEmail(photographer.getEmail());

		if(exist == null)
		{
			Photographer p = new Photographer();
			p.setName(photographer.getName());
			p.setEmail(photographer.getEmail());
			p.setPassword(encoder.encode(photographer.getPassword()));
			p.setPhoneNumber(photographer.getPhoneNumber());
			p.setServiceLocation(photographer.getServiceLocation());
			p.setExperience(photographer.getExperience());
			p.setServices(photographer.getServices());
			p.setLanguages(photographer.getLanguages());
			p.setAboutMe(photographer.getAboutMe());
			emailService.sendToPhotographer(photographer.getEmail(), p);
			p.setSignupDateTime(new Date());
			p.setStatus(false);
			p.setLogin(false);
			return repo.save(p);
		}
		else
		{
			return null;
		}
	}


	@Override
	public Photographer photographerSignin(String email, String password) {

		Photographer p = repo.findByEmail(email);//find the user with the email provided
		
		if(p != null)//check if the user with email is present
		{
			if(p.isStatus())//check if the otp is verified or not
			{
				if(password.equals(p.getPassword()) || encoder.matches(password, p.getPassword()))//check password matches or not
				{
					p.setLogin(true);
					return repo.save(p);
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

		Photographer p = repo.findByEmail(email);//find the user with the provided email
		if(p != null)
		{
			if(p.getSignupVerificationKey() == otp)//check the otp present in the database is equal to otp provided by the user
			{
				p.setStatus(true);//set the otp status as true
				p.setSignupVerificationKey(0);
				repo.save(p);//update the details to the database
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
	public List<Albums> saveAlbum(MultipartFile[] file, String category, String photographerName) throws Exception {

		List<Albums> album = new ArrayList<Albums>(file.length);
		Optional<Photographer> photographer = repo.findByName(photographerName);
		Photographer p = photographer.get();
		if(photographer.isPresent())
		{
			for(MultipartFile photo : file)
			{
				Albums a = storageService.uplodeImage(photo, category, p);
				album.add(a);
			}
			p.setAlbums(album);
			repo.save(p);
			return album;
		}
		return null;
	}


	@Override
	public List<Albums> downlodeAlbum(String email) {
		List<Albums> ab =  new ArrayList<>();
		Photographer p = repo.findByEmail(email);
		List<Albums> album = p.getAlbums();
		for(Albums a : album)
		{
			if(!a.getCategory().equals("equipment"))
			{
				a.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
				ab.add(a);
			}
		}
		return ab;
	}


	@Override
	public List<Albums> downlodeEquipments(String email) {
		List<Albums> ab =  new ArrayList<>();
		Photographer p = repo.findByEmail(email);
		List<Albums> album = p.getAlbums();
		for(Albums a : album)
		{
			if(a.getCategory().equals("equipment"))
			{
				a.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
				ab.add(a);
			}
		}
		return ab;
	}


	@Override
	public String deletePhoto(int id) {
		
		Albums album = albumRepo.findById(id);
//		Photographer photographer = album.getPhotographer();
		albumRepo.delete(album);
		return "Deleted";
		
	}


	@Override
	public byte[] changeProfilePhoto(MultipartFile file, String email) throws Exception {
		Photographer photographer = repo.findByEmail(email);
		photographer.setProfilePhoto(ImageUtils.compressImage(file.getBytes()));
		repo.save(photographer);
		return ImageUtils.decompressImage(photographer.getProfilePhoto());
	}


	@Override
	public String updateBasicInfo(PhotographerUpdateDto photographer) {
		Photographer p = repo.findByEmail(photographer.getEmail());
		p.setName(photographer.getName());
		p.setPhoneNumber(photographer.getPhoneNumber());
		p.setServiceLocation(photographer.getServiceLocation());
		p.setLanguages(photographer.getLanguages());
		p.setServices(photographer.getServices());
		p.setExperience(photographer.getExperience());
		p.setAboutMe(photographer.getAboutMe());
		repo.save(p);
		return "information updated";
	}

	@Override
	public String generateResetPasswordOtp(String emailId) {
		Photographer photographer = repo.findByEmail(emailId);
		if (photographer != null) {
			emailService.sendResetPasswordOtpToPhotographer(photographer.getEmail(), photographer);
		}
		return "Invalid Email Id";
	}


	@Override
	public String resetPassword(ResetPasswordDto resetPasswordDto) {
		Photographer p = repo.findByEmail(resetPasswordDto.getEmailId());
		if(p != null)
		{
			if (p.getResetPasswordVerificationKey() == resetPasswordDto.getOtp())
			{
				if (encoder.matches(resetPasswordDto.getOldPassword(), p.getPassword())){
					p.setPassword(encoder.encode(resetPasswordDto.getNewPassword()));
					repo.save(p);
				}
				else {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Password");
				}
				return "password updated";
			}
			else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Otp");
			}
		}
		return "wrong email";
	}
}