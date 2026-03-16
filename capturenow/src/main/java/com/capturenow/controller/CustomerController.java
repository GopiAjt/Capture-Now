package com.capturenow.controller;

import com.capturenow.config.ImageUtils;
import com.capturenow.dto.*;
import com.capturenow.module.*;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.service.*;
import com.capturenow.serviceimpl.JwtService;
import com.capturenow.serviceimpl.PhotographerSorter;
import com.capturenow.serviceimpl.RefreshTokenService;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Log4j2
@RestController
@Data
@RequestMapping(path = "/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private JwtService jwtservice;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private PhotographerCardDto photographerCardDto;

    @Autowired
    private SearchBarService searchBarService;

    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private PhotographerSorter photographerSorter;

    @Autowired
    private final AlbumService albumService;

    @Qualifier("customer")
    @Autowired
    private final AuthenticationManager authenticationManager;

    /**
     * Simple ping endpoint for uptime monitors or CI cron jobs.
     * Responds with HTTP 200 and body "OK".
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping(path = "/signup", consumes = "application/json")
    public ResponseEntity<?> customerSignUp(@RequestBody CustomerSignupDto c, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Customer customer = service.customerRegister(c);
            return new ResponseEntity<>(customer, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // If an exception is thrown, return a bad request with the exception message
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // For any other errors, return an internal server error
            return new ResponseEntity<>("An unexpected error occurred. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping(path = "/validate")
    ResponseEntity<?> validateEmail(@RequestParam String email, @RequestParam Integer otp) {
        Boolean b = service.validateEmail(email, otp);
        if (b) {
            return new ResponseEntity<>(b, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(b, HttpStatus.FORBIDDEN);
    }

    @GetMapping(path = "/signin")
    public ResponseEntity<CustomerDto> customerLogin(@RequestParam String email, @RequestParam String password) {
        Customer c = service.customerLogin(email, password);
        if (c != null) {
            CustomerDto customerDto = mapCustomerToDto(c);
            return ResponseEntity.ok(customerDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private CustomerDto mapCustomerToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setPhoneNo(customer.getPhoneNo());
        customerDto.setAuthToken(customer.getAuthToken());
        customerDto.setFavorites(getPhotographerNames(favoritesService.getFavoritesByCustomerId(customer.getId())));
        log.info(customer.getFavorites());
        log.info(customerDto.getFavorites());
        if (customer.getProfilePhoto() != null) {
            customerDto.setProfilePhoto(ImageUtils.decompressImage(customer.getProfilePhoto()));
        }
        return customerDto;
    }

    private List<String> getPhotographerNames(List<Favorites> favorites) {
        if (favorites != null) {
            return favorites.stream()
                    .map(favorite -> favorite.getPhotographer().getId())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList(); // or return null if you prefer
    }

    @Autowired
    private RefreshTokenService refreshTokenService;

    @GetMapping(path = "/authtoken")
    public JwtResponseDto authAndGetToken(@RequestParam String email, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        log.info(authentication.isAuthenticated());
        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshTokenForCustomer(email);
            return JwtResponseDto.builder()
                    .accessToken(jwtservice.ganarateToken(email))
                    .token(refreshToken.getToken())
                    .build();
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/refreshToken")
    public JwtResponseDto refreshToken(@RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
        return refreshTokenService.findByToken(tokenRefreshRequestDto.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getCustomer)
                .map(customer -> {
                    String accessToken = jwtservice.ganarateToken(customer.getEmail());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(tokenRefreshRequestDto.getToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }


    @GetMapping(path = "/getPhotographers/{offset}/{pageSize}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Page<PhotographerCardDto>> getAllPhotographers(@PathVariable int offset, @PathVariable int pageSize) {
        return new ResponseEntity<Page<PhotographerCardDto>>(service.getPhotographersByPagination(offset, pageSize), HttpStatus.OK);
    }

    @GetMapping(path = "/getPhotographersIndex/{offset}/{pageSize}")
    public ResponseEntity<Page<PhotographerCardDto>> getAllPhotographersIndex(@PathVariable int offset, @PathVariable int pageSize) {
        return new ResponseEntity<Page<PhotographerCardDto>>(service.getPhotographersByPagination(offset, pageSize), HttpStatus.OK);
    }

    @GetMapping(path = "/getPhotographerById")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<PhotographerResponseDto> getPhotographerByEmail(@RequestParam String id) {
        return new ResponseEntity<PhotographerResponseDto>(service.getPhotographerById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/getAlbumsById")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Page<Albums>> getAlbumByEmail(@RequestParam String id,
                                                        @RequestParam int offset,
                                                        @RequestParam int pageSize) {
        return new ResponseEntity<>(albumService.downloadAlbum(id, offset, pageSize), HttpStatus.OK);
    }

    @GetMapping(path = "/getEquipmentsById")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Page<Albums>> getEquipmentsByEmail(@RequestParam String id,
                                                             @RequestParam int offset,
                                                             @RequestParam int pageSize) {
        return new ResponseEntity<>(albumService.downloadEquipments(id, offset, pageSize), HttpStatus.OK);
    }

    @PostMapping(path = "/addReview")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> addRating(@RequestBody RatingDTO ratingDTO) {
        if (service.addReview(ratingDTO)) {
            // You can add validation logic here to ensure that the rating value is within a valid range.
            // You can also add authentication and authorization checks to make sure the customer is authorized to submit a rating.
            return new ResponseEntity<>("Rating added successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to add rating", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getReviewsById")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<RatingResponseDTO>> getRatings(@RequestParam String id) {
        return new ResponseEntity<List<RatingResponseDTO>>(service.getRatingsById(id), HttpStatus.OK);
    }

    @DeleteMapping(path = "/deleteReviewsById")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Boolean> deleteRatings(@RequestParam String ratingId) {
        return new ResponseEntity<Boolean>(service.deleteRatingById(ratingId), HttpStatus.OK);
    }

    @GetMapping(path = "/getPackages")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Packages>> getPackagesByEmail(@RequestParam String email) {
        return new ResponseEntity<List<Packages>>(packageService.getAllPackages(email), HttpStatus.OK);
    }

    @PostMapping(path = "/createBooking")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<String>(bookingService.createBooking(bookingRequestDto), HttpStatus.OK);
    }

    @GetMapping(path = "/getBookingStatus")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<BookingResponseStatusDto>> getBookingStatus(@RequestParam String emailId) {
        return new ResponseEntity<List<BookingResponseStatusDto>>(bookingService.getBookingStatus(emailId), HttpStatus.OK);
    }

    @DeleteMapping(path = "/cancelBooking")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> cancelBooking(@RequestParam String bookingId){
        return new ResponseEntity<String>(bookingService.cancelBooking(bookingId), HttpStatus.OK);
    }

    @PostMapping(path = "/updateDetails")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<CustomerUpdateDto> updateCustomerDetails(@RequestBody CustomerUpdateDto customerUpdateDto) {
        return new ResponseEntity<CustomerUpdateDto>(service.updateCustomerDetails(customerUpdateDto), HttpStatus.OK);
    }

    @GetMapping(path = "/resetPasswordOtp")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Boolean> resetPasswordOtp(@RequestParam String emailId){
        log.info("sending");
        return new ResponseEntity<Boolean>(service.generateResetPasswordOtp(emailId), HttpStatus.OK);
    }

    @PostMapping(path = "/resetPassword")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return new ResponseEntity<String>(service.resetPassword(resetPasswordDto), HttpStatus.OK);
    }

    @PostMapping(path = "/forgotPasswordOtp")
    public ResponseEntity<Boolean> forgotPasswordOtp(@RequestParam String emailId){
        return new ResponseEntity<Boolean>(service.generateResetPasswordOtp(emailId), HttpStatus.OK);
    }

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String emailId, @RequestParam String newPassword, @RequestParam Integer otp){
        return new ResponseEntity<>(service.forgotPassword(emailId, newPassword, otp), HttpStatus.OK);
    }

    @PostMapping(path = "/changePhoto", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ROLE_USER')")
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

    @GetMapping(path = "/searchByPreference")
    public ResponseEntity<Page<PhotographerCardDto>> searchByPre(@RequestParam String pre, @RequestParam int offset, @RequestParam int pageSize){
        return new ResponseEntity<Page<PhotographerCardDto>>(searchBarService.searchByPreference(pre, offset, pageSize), HttpStatus.OK);
    }

    @GetMapping(path = "/searchByLocation")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<PhotographerCardDto>> searchByLocation(@RequestParam String location){
        return new ResponseEntity<List<PhotographerCardDto>>(searchBarService.searchByLocation(location), HttpStatus.OK);
    }

    @GetMapping(path = "/addToFavorites")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> addToFavorites(@RequestParam String customerId, @RequestParam String photographerId){
        log.info("adding");
        return new ResponseEntity<String>(favoritesService.addToFavorites(customerId, photographerId), HttpStatus.OK);
    }

    @DeleteMapping(path = "/removeFromFavorites")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Boolean> removeFromFavorites(@RequestParam String customerId,
                                                      @RequestParam String photographerId) {
        Boolean result = favoritesService.removeFromFavorites(customerId, photographerId);
        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
    }

    @GetMapping(path = "/getAllFavorites")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<FavResponseDto>> getFavData(@RequestParam String email){
        return new ResponseEntity<List<FavResponseDto>>(favoritesService.getAllFavorites(email), HttpStatus.OK);
    }

    @GetMapping(path = "/addFilter/{offset}/{pageSize}/{field}")
    public ResponseEntity<Page<PhotographerCardDto>> addFilter(@PathVariable Integer offset, @PathVariable Integer pageSize, @PathVariable Integer field){
        return new ResponseEntity<Page<PhotographerCardDto>>(service.addFilter(offset, pageSize, field), HttpStatus.OK);
    }
}
