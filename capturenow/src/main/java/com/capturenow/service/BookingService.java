package com.capturenow.service;

import java.time.LocalDateTime;

public interface BookingService {
    String createBooking(LocalDateTime startDate, LocalDateTime endDate, int packageId, String customerId, String photographerId);
}
