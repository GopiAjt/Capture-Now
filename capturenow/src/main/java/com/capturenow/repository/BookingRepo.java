package com.capturenow.repository;

import com.capturenow.module.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<Booking, String> {

}
