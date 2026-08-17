package com.project.hotelmanagement.scheduler;

import com.project.hotelmanagement.Entity.Booking;
import com.project.hotelmanagement.Entity.enums.BookingStatus;
import com.project.hotelmanagement.repository.BookingRepository;
import com.project.hotelmanagement.service.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final BookingServiceImpl bookingService;

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredBooking() {

        List<Booking> bookings = bookingRepository.findByBookingStatus(BookingStatus.RESERVED);

        for (Booking booking : bookings) {

            if (bookingService.hasBookingExpired(booking)) {

                bookingService.expireBooking(booking);
            }
        }
    }

}
