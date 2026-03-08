package com.capturenow.serviceimpl;

import com.capturenow.config.ImageUtils;
import com.capturenow.dto.*;
import com.capturenow.email.EmailService;
import com.capturenow.module.*;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.repository.RatingRepo;
import com.capturenow.service.AlbumService;
import com.capturenow.service.CustomerService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Data
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo repo;//login to talk with the database

    private final PhotographerRepo photographerRepo;

    private final RatingRepo ratingRepo;

    private final EmailService emailService;//logic to send the email

    private final Photographer photographer;

    private final PhotographerSorter photographerSorter;



    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();//to encode and decode the password
    private final PhotographerCardDto photographerCardDto;

    @Override
    public Customer customerRegister(CustomerSignupDto c) {
        // Check if a user with the given email already exists
        Customer exist = repo.findByEmail(c.getEmail());

        if (exist != null) {
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "Email already exists. Please verify your email or use a different one.");
        }

        Customer customer = new Customer();
        customer.setName(c.getName());
        customer.setEmail(c.getEmail());
        customer.setPhoneNo(c.getPhoneNo());

        try {
            // Send OTP email
            emailService.sendToCustomer(c.getEmail(), customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }

        // Encode the password and set the necessary fields
        customer.setPassword(encoder.encode(c.getPassword()));
        customer.setSignupDateTime(new Date());
        customer.setStatus(false);  // Set OTP verification status as false
        customer.setLogin(false);   // Set login status as false

        // Save the new customer to the database
        return repo.save(customer);
    }


    @Override
    public Customer customerLogin(String email, String password) {
        Customer c = repo.findByEmail(email);

        if (c == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid email or password");
        }

        if (!c.isStatus()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please verify your account");
        }

        if (!encoder.matches(password, c.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password");
        }

        c.setLogin(true);
        return repo.save(c);
    }


    @Override
    public Boolean validateEmail(String email, Integer otp) {

        Customer c = repo.findByEmail(email);//find the user with the provided email
        if (c != null) {
            if (c.getSignupVerificationKey() == otp)//check the otp present in the database is equal to otp provided by the user
            {
                c.setStatus(true);//set the otp status as trues
                repo.save(c);//update the details to the database
                return true;
            } else {
                return false;
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Valid User");
        }
    }

    @Override
    public List<PhotographerCardDto> getAllPhotographers() {
        return null;
    }

    @Override
    public Page<PhotographerCardDto> getPhotographersByPagination(int offset, int pageSize) {
        // Fetch photographers using pagination
        Page<Photographer> photographerPage = photographerRepo.findAll(PageRequest.of(offset, pageSize));

        // Convert photographers to PhotographerCardDto and build Page
        List<PhotographerCardDto> cards = photographerPage.getContent().stream()
                .map(this::convertPhotographerToCardDto)
                .collect(Collectors.toList());

        return new PageImpl<>(cards, photographerPage.getPageable(), photographerPage.getTotalElements());
    }

    @Override
    public PhotographerResponseDto getPhotographerById(String id) {
        Optional<Photographer> photographer = photographerRepo.findById(id);

        if (photographer.isPresent()) {
            PhotographerResponseDto p = new PhotographerResponseDto();
            p.setName(photographer.get().getName());
            p.setPId(photographer.get().getId());
//			p.setPhoneNumber(photographer.getPhoneNumber());
//            p.setEmail();
            p.setExperience(photographer.get().getExperience());
            p.setLanguages(photographer.get().getLanguages());
            p.setAboutMe(photographer.get().getAboutMe());
            p.setServices(photographer.get().getServices());
            p.setServiceLocation(photographer.get().getServiceLocation());
            p.setPackages(photographer.get().getPackages());
            p.setAvgRating(photographer.get().getAvgRating());
            if (photographer.get().getProfilePhoto() != null) {
                p.setProfilePhoto(ImageUtils.decompressImage(photographer.get().getProfilePhoto()));
            }
            return p;
        }
        return null;
    }

    @Override
    public Page<AlbumResponseDto> getAlbumByEmail(String email, int offset, int pageSize) {
        Photographer p = photographerRepo.findByEmail(email);
        List<AlbumResponseDto> albumResponseDtos = new ArrayList<>();
        if (photographer == null) {
            return new PageImpl<>(albumResponseDtos, PageRequest.of(offset / pageSize, pageSize), 0);
        }

        List<Albums> albums = photographer.getAlbums();

        for (Albums a : albums) {
            if (!a.getCategory().equals("equipment")) {
                AlbumResponseDto albumResponseDto = new AlbumResponseDto();
                albumResponseDto.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
                albumResponseDto.setCategory(a.getCategory());
                albumResponseDto.setName(a.getName());
                albumResponseDtos.add(albumResponseDto);
            }
        }

        Pageable pageable = PageRequest.of(offset / pageSize, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), albumResponseDtos.size());
        List<AlbumResponseDto> pagedList = albumResponseDtos.subList(start, end);
        return new PageImpl<>(pagedList, pageable, albumResponseDtos.size());
    }

    public List<AlbumResponseDto> getEquipmentsByEmail(String email) {
        Photographer photographer = photographerRepo.findByEmail(email);
        List<AlbumResponseDto> albumResponseDtos = new ArrayList<>();
        if (photographer != null) {
            List<Albums> albums = photographer.getAlbums();

            for (Albums a : albums) {
                if (a.getCategory().equals("equipment")) {
                    AlbumResponseDto albumResponseDto = new AlbumResponseDto();
                    albumResponseDto.setPhoto(ImageUtils.decompressImage(a.getPhoto()));
                    albumResponseDto.setName(a.getName());
                    albumResponseDto.setCategory(a.getCategory());
                    albumResponseDtos.add(albumResponseDto);
                }
            }

        }
        return albumResponseDtos;
    }

    @Override
    public boolean addReview(RatingDTO ratingDTO) {
        try {
            log.info("adding");
            PhotographerRatings newRating = new PhotographerRatings();
            newRating.setCustomer(repo.findByEmail(ratingDTO.getCustomerId()));
            newRating.setPhotographer(photographerRepo.findById(ratingDTO.getPhotographerId()).get());
            newRating.setRatings(ratingDTO.getRating());
            newRating.setComments(ratingDTO.getComment());
            newRating.setRatingDate(ratingDTO.getDateTime());
            ratingRepo.save(newRating);

            // Calculate average rating for the photographer
            Photographer photographer = newRating.getPhotographer();
            // Format the new average rating to 2 decimal points
            BigDecimal formattedAvgRating = BigDecimal.valueOf(ratingRepo.getAverageRatingByPhotographer(photographer)).setScale(2, RoundingMode.HALF_UP);
            photographer.setAvgRating(formattedAvgRating.doubleValue());

            photographerRepo.save(photographer);
            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    @Override
    public List<RatingResponseDTO> getRatingsById(String id) {
        Optional<Photographer> photographer = photographerRepo.findById(id);
        List<RatingResponseDTO> ratingResponseDTO = new ArrayList<>();
        List<PhotographerRatings> photographerRatings = photographer.get().getPhotographerRatings();
        for (PhotographerRatings ratings : photographerRatings) {
            RatingResponseDTO responseDTO = new RatingResponseDTO();
            responseDTO.setRatingId(ratings.getId());
            responseDTO.setRating(ratings.getRatings());
            responseDTO.setComment(ratings.getComments());
            responseDTO.setCustomerName(ratings.getCustomer().getName());
            responseDTO.setCustomerId(ratings.getCustomer().getId());
            responseDTO.setRatingDate(ratings.getRatingDate());
            if (null != ratings.getCustomer().getProfilePhoto()) {
                responseDTO.setCustomerProfilePhoto(ImageUtils.decompressImage(ratings.getCustomer().getProfilePhoto()));
            }
            ratingResponseDTO.add(responseDTO);
        }
        return ratingResponseDTO;
    }

    @Override
    public Boolean deleteRatingById(String ratingId){
        Optional<PhotographerRatings> photographerRatings = ratingRepo.findById(ratingId);
        if (photographerRatings.isPresent()){
            ratingRepo.deleteById(ratingId);
            return true;
        }
        return false;
    }

    @Override
    public CustomerUpdateDto updateCustomerDetails(CustomerUpdateDto customerUpdateDto) {
        Customer customer = repo.findByEmail(customerUpdateDto.getEmail());
        if (customer != null) {
            customer.setName(customerUpdateDto.getName());
            customer.setEmail(customerUpdateDto.getEmail());
            customer.setPhoneNo(customerUpdateDto.getPhoneNo());
            repo.save(customer);
        }
        return customerUpdateDto;
    }

    @Override
    public Boolean generateResetPasswordOtp(String emailId) {

        Customer customer = repo.findByEmail(emailId);
        if (customer != null) {
            emailService.sendResetPasswordOtpToCustomer(customer.getEmail(), customer);
            return true;
        }
        return false;
    }

    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto) {
        Customer customer = repo.findByEmail(resetPasswordDto.getEmailId());
        if (customer != null) {
            if (customer.getResetPasswordVerificationKey() == resetPasswordDto.getOtp()) {
                if (encoder.matches(resetPasswordDto.getOldPassword(), customer.getPassword())) {
                    customer.setPassword(encoder.encode(resetPasswordDto.getNewPassword()));
                    repo.save(customer);
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

    @Override
    public String forgotPassword(String emailId, String newPassword, Integer otp) {
        Customer customer = repo.findByEmail(emailId);
        if (customer != null){
            if(customer.getResetPasswordVerificationKey() == otp){
                customer.setPassword(encoder.encode(newPassword));
                repo.save(customer);
            }else {
                throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Otp");
            }
            return "password updated";
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Email Id");
        }
    }


    @Override
    public byte[] changeProfilePhoto(MultipartFile file, String email) throws Exception {
        Customer customer = repo.findByEmail(email);
        customer.setProfilePhoto(ImageUtils.compressImage(file.getBytes()));
        repo.save(customer);
        return ImageUtils.decompressImage(customer.getProfilePhoto());
    }

    @Override
    public Page<PhotographerCardDto> addFilter(Integer offset, Integer pageSize, Integer field){

        switch (field){
            case 0:
                Page<Photographer> photographerPageCostAsc = photographerRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(Sort.Direction.ASC,"startsWith")));
                // Convert photographers to PhotographerCardDto and build Page
                List<PhotographerCardDto> cards0 = photographerPageCostAsc.getContent().stream()
                        .map(this::convertPhotographerToCardDto)
                        .collect(Collectors.toList());
                return new PageImpl<>(cards0, photographerPageCostAsc.getPageable(), photographerPageCostAsc.getTotalElements());

            case 1:
                Page<Photographer> photographerPageCostAscDesc = photographerRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(Sort.Direction.DESC,"startsWith")));
                List<PhotographerCardDto> cards1 = photographerPageCostAscDesc.getContent().stream()
                        .map(this::convertPhotographerToCardDto)
                        .collect(Collectors.toList());
                return new PageImpl<>(cards1, photographerPageCostAscDesc.getPageable(), photographerPageCostAscDesc.getTotalElements());

            case 2:
                Page<Photographer> photographerPageRatingAsc = photographerRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(Sort.Direction.ASC,"avgRating")));
                List<PhotographerCardDto> cards2 = photographerPageRatingAsc.getContent().stream()
                        .map(this::convertPhotographerToCardDto)
                        .collect(Collectors.toList());
                return new PageImpl<>(cards2, photographerPageRatingAsc.getPageable(), photographerPageRatingAsc.getTotalElements());

            case 3:
                Page<Photographer> photographerPageRatingDesc = photographerRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(Sort.Direction.DESC,"avgRating")));
                List<PhotographerCardDto> cards3 = photographerPageRatingDesc.getContent().stream()
                        .map(this::convertPhotographerToCardDto)
                        .collect(Collectors.toList());
                return new PageImpl<>(cards3, photographerPageRatingDesc.getPageable(), photographerPageRatingDesc.getTotalElements());
        }
        return null;
    }


    // Helper method to convert Photographer to PhotographerCardDto
    private PhotographerCardDto convertPhotographerToCardDto(Photographer photographer) {
        List<Packages> packages = photographer.getPackages();
        packages.sort(Comparator.comparingDouble(Packages::getEventRate));

        byte[] profilePhoto = photographer.getProfilePhoto();
        if (profilePhoto != null) {
            profilePhoto = ImageUtils.decompressImage(profilePhoto);
        }
        return PhotographerCardDto.builder()
                .id(photographer.getId())
                .name(photographer.getName())
                .avgRating(photographer.getAvgRating())
                .services(photographer.getServices())
                .languages(photographer.getLanguages())
                .experience(photographer.getExperience())
                .serviceLocation(photographer.getServiceLocation())
                .mailId(photographer.getEmail())
                .profilePhoto(profilePhoto)
                .startsWith(packages.isEmpty() ? 0 : packages.get(0).getEventRate())
                .build();
    }
}