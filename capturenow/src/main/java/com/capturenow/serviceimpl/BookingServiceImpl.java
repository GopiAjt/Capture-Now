package com.capturenow.serviceimpl;

import com.capturenow.module.Booking;
import com.capturenow.module.Customer;
import com.capturenow.repository.BookingRepo;
import com.capturenow.repository.CustomerRepo;
import com.capturenow.repository.PackageRepo;
import com.capturenow.repository.PhotographerRepo;
import com.capturenow.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepo bookingRepo;

    @Autowired
    private final PackageRepo packageRepo;

    @Autowired
    private final PhotographerRepo photographerRepo;

    @Autowired
    private final CustomerRepo customerRepo;

    public BookingServiceImpl(BookingRepo bookingRepo, PackageRepo packageRepo, PhotographerRepo photographerRepo, CustomerRepo customerRepo) {
        this.bookingRepo = bookingRepo;
        this.packageRepo = packageRepo;
        this.photographerRepo = photographerRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public String createBooking(LocalDateTime startDate, LocalDateTime endDate, int packageId, String customerId, String photographerId) {

        Booking booking = new Booking();
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setPackages(packageRepo.findById(packageId).get());
        booking.setPhotographer(photographerRepo.findByEmail(photographerId));
        booking.setCustomer(customerRepo.findByEmail(customerId));
        bookingRepo.save(booking);
        return "saved";
    }

    @Override
    public List<Booking> getBookingStatus(String email) {
        Customer customer = customerRepo.findByEmail(email);
        if (customer != null){
            List<Booking> booking = customer.getBooking();
            return booking;
        }
        return null;
    }
}
