package com.capturenow.service;

import com.capturenow.dto.*;
import com.capturenow.module.Customer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Component
public interface CustomerService {

    Customer customerRegister(CustomerSignupDto c);

    Customer customerLogin(String email, String password);

    Boolean validateEmail(String email, Integer otp);

    List<PhotographerCardDto> getAllPhotographers();

    Page<PhotographerCardDto> getPhotographersByPagination(int offset, int pageSize);

    PhotographerResponseDto getPhotographerById(String email);

    Page<AlbumResponseDto> getAlbumByEmail(String email, int offset, int pageSize);

    List<AlbumResponseDto> getEquipmentsByEmail(String email);

    boolean addReview(RatingDTO ratingDTO);

    List<RatingResponseDTO> getRatingsById(String id);

    Boolean deleteRatingById(String ratingId);

    CustomerUpdateDto updateCustomerDetails(CustomerUpdateDto customerUpdateDto);

    Boolean generateResetPasswordOtp(String emailId);

    String resetPassword(ResetPasswordDto resetPasswordDto);

    String forgotPassword(String emailId, String newPassword, Integer otp);

    byte[] changeProfilePhoto(MultipartFile file, String email) throws Exception;

    Page<PhotographerCardDto> addFilter(Integer offset, Integer pageSize, Integer field);

}
