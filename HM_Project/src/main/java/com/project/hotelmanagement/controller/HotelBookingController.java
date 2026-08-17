package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.BookingDto;
import com.project.hotelmanagement.dto.BookingRequestDto;
import com.project.hotelmanagement.dto.GuestDto;
import com.project.hotelmanagement.dto.GuestRequestDto;
import com.project.hotelmanagement.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("hotels/bookings")
@PreAuthorize("hasRole('GUEST')")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto) {

        return ResponseEntity.ok(bookingService.createBooking(bookingRequestDto));
    }

    @PostMapping("{bookingId}/guests")
    public ResponseEntity<List<GuestDto>> guestBooking(@PathVariable Long bookingId, @RequestBody List<GuestRequestDto> guestRequestDto) {

        return ResponseEntity.ok(bookingService.guestBooking(bookingId,guestRequestDto));
    }

    @DeleteMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDto> cancelBooking (@PathVariable Long bookingId ) {

        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

}
