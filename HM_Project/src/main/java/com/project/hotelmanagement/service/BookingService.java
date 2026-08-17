package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.BookingDto;
import com.project.hotelmanagement.dto.BookingRequestDto;
import com.project.hotelmanagement.dto.GuestDto;
import com.project.hotelmanagement.dto.GuestRequestDto;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {

    BookingDto createBooking(BookingRequestDto bookingRequestDto);

    List<GuestDto> guestBooking(Long bookingId, List<GuestRequestDto> guestRequestDto);

    BookingDto cancelBooking(Long bookingId);
}
