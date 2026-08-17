package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.*;
import com.project.hotelmanagement.Entity.enums.BookingStatus;
import com.project.hotelmanagement.Entity.enums.PaymentStatus;
import com.project.hotelmanagement.dto.BookingDto;
import com.project.hotelmanagement.dto.BookingRequestDto;
import com.project.hotelmanagement.dto.GuestDto;
import com.project.hotelmanagement.dto.GuestRequestDto;
import com.project.hotelmanagement.exception.ResourceNotFoundException;
import com.project.hotelmanagement.repository.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;

    private final ModelMapper modelMapper;
    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public BookingDto createBooking(BookingRequestDto bookingRequestDto) {

        log.info("Creating Booking for hotel room : {}, date : {} - {}", bookingRequestDto.getRoomId(), bookingRequestDto.getCheckIn(), bookingRequestDto.getCheckOut());

        Room room = roomRepository.findById(bookingRequestDto.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with Id " + bookingRequestDto.getRoomId()));

        Hotel hotel = room.getHotel();

        if (!hotel.getActive()) {
            throw new IllegalStateException("Hotel is not active");
        }

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequestDto.getCheckIn(), bookingRequestDto.getCheckOut(), bookingRequestDto.getRoomCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckIn(), bookingRequestDto.getCheckOut()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available at this time!!");
        }

        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomCount());
        }
        inventoryRepository.saveAll(inventoryList);

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkIn(bookingRequestDto.getCheckIn())
                .checkOut(bookingRequestDto.getCheckOut())
                .user(getCurrentUser())
                .roomCount(bookingRequestDto.getRoomCount())
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public List<GuestDto> guestBooking(Long bookingId, List<GuestRequestDto> guestRequestDto) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with Id " +bookingId));

        User user = getCurrentUser();

        if (!booking.getUser().getId().equals(user.getId())) {

            throw new AccessDeniedException("You are not allowed to modify this booking");
        }

        List<Guest> guests = guestRequestDto.stream().map(dto-> {

            Guest guest = modelMapper.map(dto, Guest.class);

            guest.setUser(user);

            return guest;

        }).toList();

        guestRepository.saveAll(guests);

        if (booking.getGuests() == null) {
            booking.setGuests(new HashSet<>());
        }
        booking.getGuests().addAll(guests);

        bookingRepository.save(booking);

        return guests.stream().map(guest->
                modelMapper.map(guest, GuestDto.class)).toList();

    }

    @Transactional
    @Override
    public BookingDto cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with Id " + bookingId));

        User currentUser = getCurrentUser();

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Booking is already cancelled");
        }

        Payment payment = booking.getPayment();

        // Paid booking → refund first
        if (payment != null
                && payment.getPaymentStatus() == PaymentStatus.CONFIRMED) {

            if (payment.getRazorpayPaymentId() == null) {
                throw new IllegalStateException(
                        "Razorpay payment ID not found");
            }

            try {

                JSONObject refundRequest = new JSONObject();

                refundRequest.put(
                        "amount",
                        payment.getAmount()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue()
                );

                razorpayClient.payments.refund(
                        payment.getRazorpayPaymentId(),
                        refundRequest);

                payment.setPaymentStatus(
                        PaymentStatus.CANCELLED);

                paymentRepository.save(payment);

            } catch (RazorpayException e) {

                log.error("Razorpay refund failed: {}", e.getMessage(), e);

                throw new RuntimeException(
                        "Unable to refund payment: " + e.getMessage(), e);
            }
        }

        // Release reserved inventory
        List<Inventory> inventoryList =
                inventoryRepository.findByRoomIdAndDateBetween(
                        booking.getRoom().getId(),
                        booking.getCheckIn(),
                        booking.getCheckOut());

        for (Inventory inventory : inventoryList) {

            inventory.setReservedCount(
                    inventory.getReservedCount()
                            - booking.getRoomCount());
        }

        inventoryRepository.saveAll(inventoryList);

        booking.setBookingStatus(
                BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        return modelMapper.map(
                booking,
                BookingDto.class);
    }

    @Transactional
    public void expireBooking(Booking booking) {

        if (booking.getPayment() != null
                && booking.getPayment().getPaymentStatus()
                == PaymentStatus.CONFIRMED) {

            return;
        }

        List<Inventory> inventoryList =
                inventoryRepository.findByRoomIdAndDateBetween(
                        booking.getRoom().getId(),
                        booking.getCheckIn(),
                        booking.getCheckOut());

        for (Inventory inventory : inventoryList) {

            inventory.setReservedCount(
                    inventory.getReservedCount()
                            - booking.getRoomCount());
        }

        inventoryRepository.saveAll(inventoryList);

        booking.setBookingStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        log.info("Expired booking {} and released inventory",
                booking.getId());
    }

    public boolean hasBookingExpired(Booking booking) {

        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

}
