package com.capturenow.serviceimpl;

import com.capturenow.config.ImageUtils;
import com.capturenow.dto.*;
import com.capturenow.email.EmailService;
import com.capturenow.module.Albums;
import com.capturenow.module.Photographer;
import com.capturenow.module.PhotographerRatings;
import com.capturenow.repository.AlbumRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.PhotographerService;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Data
public class PhotographerServiceImpl implements PhotographerService {

    @Autowired
    private final PhotographerRepo repo;

    @Autowired
    private final EmailService emailService;//logic to send the email

    @Autowired
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private final StorageService storageService;

    @Autowired
    private final AlbumRepo albumRepo;

    @Autowired
    private final PhotographerDTO photographerDTO;

    @Override
    public Photographer photographerSignup(PhotographerRegistrationDTO photographer) {
        String cleanedEmail = photographer.getEmail().trim().toLowerCase();
        Photographer exist = repo.findByEmail(cleanedEmail);

        if (exist == null) {
            Photographer p = new Photographer();
            p.setName(photographer.getName());
            p.setEmail(cleanedEmail);
            p.setPassword(encoder.encode(photographer.getPassword()));
            p.setPhoneNumber(photographer.getPhoneNumber());
            p.setStartsWith(0.0);
            p.setSignupDateTime(new Date());
            p.setStatus(false);
            p.setLogin(false);
            int otp = EmailService.otpGanaretor();
            p.setSignupVerificationKey(otp);
            Photographer saved = repo.save(p);
            emailService.sendToPhotographer(cleanedEmail, otp);
            return saved;
        } else {
            return null;
        }
    }


    @Override
    public Photographer photographerSignin(String email, String password) {
        String cleanedEmail = email.trim().toLowerCase();
        Photographer p = repo.findByEmail(cleanedEmail);//find the user with the email provided

        if (p != null)//check if the user with email is present
        {
            if (p.isStatus())//check if the otp is verified or not
            {
                if (password.equals(p.getPassword()) || encoder.matches(password, p.getPassword()))//check password matches or not
                {
                    p.setLogin(true);
                    return repo.save(p);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Password");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Verify Your Account");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Valid Email");
        }
    }

    @Override
    public Boolean validateEmail(String email, Integer otp) {
        String cleanedEmail = email.trim().toLowerCase();
        Photographer p = repo.findByEmail(cleanedEmail);//find the user with the provided email
        if (p != null) {
            if (p.getSignupVerificationKey() == otp)//check the otp present in the database is equal to otp provided by the user
            {
                p.setStatus(true);//set the otp status as true
                p.setSignupVerificationKey(0);
                repo.save(p);//update the details to the database
                return true;
            } else {
                return false;
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Valid User: " + cleanedEmail);
        }
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
        if (p != null) {
            if (p.getResetPasswordVerificationKey() == resetPasswordDto.getOtp()) {
                if (encoder.matches(resetPasswordDto.getOldPassword(), p.getPassword())) {
                    p.setPassword(encoder.encode(resetPasswordDto.getNewPassword()));
                    repo.save(p);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Password");
                }
                return "password updated";
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Otp");
            }
        }
        return "wrong email";
    }

    //    to display  reviews to photographer profile
    @Override
    public List<RatingResponseDTO> getRatingsByEmail(String email) {
        Photographer photographer = repo.findByEmail(email);
        List<RatingResponseDTO> ratingResponseDTO = new ArrayList<RatingResponseDTO>();
        List<PhotographerRatings> photographerRatings = photographer.getPhotographerRatings();
        for (PhotographerRatings ratings : photographerRatings) {
            RatingResponseDTO responseDTO = new RatingResponseDTO();
            responseDTO.setRating(ratings.getRatings());
            responseDTO.setComment(ratings.getComments());
            responseDTO.setCustomerName(ratings.getCustomer().getName());
            responseDTO.setRatingDate(ratings.getRatingDate());
            if (null != ratings.getCustomer().getProfilePhoto()) {
                responseDTO.setCustomerProfilePhoto(ImageUtils.decompressImage(ratings.getCustomer().getProfilePhoto()));
            }
            ratingResponseDTO.add(responseDTO);
        }
        return ratingResponseDTO;
    }

    @Override
    public List<PhotographerCardDto> searchPhotographer(String query) {
        List<Photographer> photographers = repo.searchPhotographer(query);
        List<PhotographerCardDto> cardDtos = new ArrayList<>(photographers.size());

        for (Photographer photographer : photographers) {
            PhotographerCardDto cardDto = new PhotographerCardDto();
            cardDto.setName(photographer.getName());
//            cardDto.setEmail(photographer.getEmail());
            cardDto.setEmail(photographer.getEmail());
            cardDto.setServices(photographer.getServices());
            cardDto.setLanguages(photographer.getLanguages());
            cardDto.setServiceLocation(photographer.getServiceLocation());
            cardDto.setExperience(photographer.getExperience());
            // Set other fields
            cardDtos.add(cardDto);
        }
        return cardDtos;
    }

    @Override
    public String forgotPassword(String emailId, String newPassword, Integer otp) {
        String cleanedEmail = emailId.trim().toLowerCase();
        Photographer photographer = repo.findByEmail(cleanedEmail);
        if (photographer != null) {
            if (photographer.getResetPasswordVerificationKey() == otp) {
                photographer.setPassword(encoder.encode(newPassword));
                repo.save(photographer);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Otp");
            }
            return "password updated";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email Id");
        }
    }
}