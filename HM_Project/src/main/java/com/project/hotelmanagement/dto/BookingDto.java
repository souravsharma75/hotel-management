package com.project.hotelmanagement.dto;

import com.project.hotelmanagement.Entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;

    private Integer roomCount;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;
}
