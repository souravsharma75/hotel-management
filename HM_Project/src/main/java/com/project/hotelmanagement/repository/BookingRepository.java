package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.Entity.Booking;
import com.project.hotelmanagement.Entity.Payment;
import com.project.hotelmanagement.Entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPayment(Payment payment);

    List<Booking> findByBookingStatus(BookingStatus bookingStatus);
}
