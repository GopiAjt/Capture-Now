package com.capturenow.serviceimpl;

import com.capturenow.dto.BookingRequestDto;
import com.capturenow.dto.BookingResponseStatusDto;
import com.capturenow.email.EmailService;
import com.capturenow.module.Booking;
import com.capturenow.module.Customer;
import com.capturenow.module.Photographer;
import com.capturenow.repository.BookingRepo;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.PackageRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.BookingService;
import jakarta.persistence.PreRemove;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.boot.jaxb.mapping.marshall.TemporalTypeMarshalling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
@Data
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepo bookingRepo;

    @Autowired
    private final PackageRepo packageRepo;

    @Autowired
    private final PhotographerRepo photographerRepo;

    @Autowired
    private final CustomerRepo customerRepo;

    @Autowired
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepo bookingRepo, PackageRepo packageRepo, PhotographerRepo photographerRepo,
                              CustomerRepo customerRepo, EmailService emailService) {
        this.bookingRepo = bookingRepo;
        this.packageRepo = packageRepo;
        this.photographerRepo = photographerRepo;
        this.customerRepo = customerRepo;
        this.emailService = emailService;
    }

    @Override
    public String createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStartDate(bookingRequestDto.getStartDate());
        booking.setEndDate(bookingRequestDto.getEndDate());
        booking.setBookedDateTime(bookingRequestDto.getBookedDateTime());
        booking.setPackages(packageRepo.findById(bookingRequestDto.getPackageId()).get());
        Photographer photographer = photographerRepo.findById(bookingRequestDto.getPhotographerId()).get();
        booking.setStatus("Created");
        booking.setPhotographer(photographer);
        booking.setCustomer(customerRepo.findByEmail(bookingRequestDto.getCustomerId()));
        emailService.sendBookNotificationToPhotographer(photographer.getEmail(), photographer);
        bookingRepo.save(booking);
        return "saved";
    }

    @Override
    public List<BookingResponseStatusDto> getBookingStatus(String email) {
        Customer customer = customerRepo.findByEmail(email);

        if (customer != null) {
            List<Booking> bookings = bookingRepo.findByCustomer_Id(customer.getId());
            List<BookingResponseStatusDto> statusDtoList = new ArrayList<>();

            for (Booking booking : bookings) {
                BookingResponseStatusDto statusDto = new BookingResponseStatusDto();
                statusDto.setBookingId(booking.getBookingId());
                statusDto.setCustomerName(customer.getName());
                statusDto.setPhotographerName(booking.getPhotographer().getName());
                statusDto.setBookedPackage(booking.getPackages());
                statusDto.setStatus(booking.getStatus());
                statusDto.setStartDate(booking.getStartDate());
                statusDto.setEndDate(booking.getEndDate());
                statusDto.setBookedDateTime(booking.getBookedDateTime());

                statusDtoList.add(statusDto); // Add statusDto to the list
            }
            return statusDtoList;
        }
        return null;
    }

    @Override
    public List<BookingResponseStatusDto> getBookingStatusPhotographer(String email) {
        Photographer photographer = photographerRepo.findByEmail(email);
        if (photographer != null)
        {
            List<Booking> bookings = bookingRepo.findByPhotographer_Id(photographer.getId());
            List<BookingResponseStatusDto> statusDtoList = new ArrayList<>();

            for (Booking booking: bookings){
                BookingResponseStatusDto statusDto = new BookingResponseStatusDto();
                statusDto.setBookingId(booking.getBookingId());
                statusDto.setCustomerName(booking.getCustomer().getName());
                statusDto.setPhotographerName(booking.getPhotographer().getName());
                statusDto.setBookedPackage(booking.getPackages());
                statusDto.setStatus(booking.getStatus());
                statusDto.setStartDate(booking.getStartDate());
                statusDto.setEndDate(booking.getEndDate());

                statusDtoList.add(statusDto);
            }
            return statusDtoList;
        }
        return null;
    }

    @Override
    public Boolean acceptDeclineBooking(Boolean status, String bookingId) {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (booking.isPresent()){
            Customer customer = booking.get().getCustomer();
            if (status){
                emailService.sendBookingConfirmationToCustomer(customer.getEmail(), customer);
                bookingRepo.updateBookingStatusById(bookingId, "Accepted");
            }else {
                emailService.sendBookingDeclineToCustomer(customer.getEmail(), customer);
                bookingRepo.updateBookingStatusById(bookingId, "Declined");
            }
            return true;
        }
        return false;
    }

    //For Customer
    @Override
    public String cancelBooking(String bookingId) {
        // Step 1: Retrieve the Booking entity by its booking ID
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        booking.ifPresent(bookingRepo::delete);
        return "canceled";
    }

}
