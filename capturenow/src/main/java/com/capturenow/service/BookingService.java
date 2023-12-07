package com.capturenow.service;

import com.capturenow.module.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    String createBooking(LocalDateTime startDate, LocalDateTime endDate, int packageId, String customerId, String photographerId);

    List<Booking> getBookingStatus(String email);
}
