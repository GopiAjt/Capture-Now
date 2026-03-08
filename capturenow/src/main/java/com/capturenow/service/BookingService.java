package com.capturenow.service;

import com.capturenow.dto.BookingRequestDto;
import com.capturenow.dto.BookingResponseStatusDto;
import com.capturenow.module.Booking;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    String createBooking(@RequestBody BookingRequestDto bookingRequestDto);

    List<BookingResponseStatusDto> getBookingStatus(String email);

    List<BookingResponseStatusDto> getBookingStatusPhotographer(String email);

    Boolean acceptDeclineBooking(Boolean status, String bookingId);

    String cancelBooking(String bookingId);

}
