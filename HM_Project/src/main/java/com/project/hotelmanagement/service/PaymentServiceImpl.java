package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Booking;
import com.project.hotelmanagement.Entity.Payment;
import com.project.hotelmanagement.Entity.User;
import com.project.hotelmanagement.Entity.enums.BookingStatus;
import com.project.hotelmanagement.Entity.enums.PaymentStatus;
import com.project.hotelmanagement.dto.CreateOrderResponseDto;
import com.project.hotelmanagement.dto.PaymentFailedRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentResponseDto;
import com.project.hotelmanagement.exception.ResourceNotFoundException;
import com.project.hotelmanagement.repository.BookingRepository;
import com.project.hotelmanagement.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayClient razorpayClient;

    @Transactional
    @Override
    public CreateOrderResponseDto createOrder(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        User currentUser = getCurrentUser();

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to make payment for this booking");
        }

        Payment payment = booking.getPayment();

        // Payment already exists
        if (payment != null) {

            // Already paid
            if (payment.getPaymentStatus() == PaymentStatus.CONFIRMED) {
                throw new IllegalStateException(
                        "Payment already completed for this booking");
            }

            // Payment is still pending → reuse existing Razorpay order
            return CreateOrderResponseDto.builder()
                    .orderId(payment.getRazorpayOrderId())
                    .amount(payment.getAmount())
                    .currency("INR")
                    .keyId(keyId)
                    .build();
        }

        // Booking must be RESERVED
        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException(
                    "Payment cannot be created for this booking");
        }

        long days = ChronoUnit.DAYS.between(
                booking.getCheckIn(),
                booking.getCheckOut()) + 1;

        BigDecimal amount = booking.getRoom()
                .getBasePrice()
                .multiply(BigDecimal.valueOf(booking.getRoomCount()))
                .multiply(BigDecimal.valueOf(days));

        try {

            JSONObject options = new JSONObject();

            options.put(
                    "amount",
                    amount.multiply(BigDecimal.valueOf(100))
            );

            options.put("currency", "INR");

            options.put(
                    "receipt",
                    "booking_" + booking.getId()
            );

            Order order = razorpayClient.orders.create(options);

            Payment newPayment = new Payment();

            newPayment.setRazorpayOrderId(order.get("id"));
            newPayment.setPaymentStatus(PaymentStatus.PENDING);
            newPayment.setAmount(amount);

            paymentRepository.save(newPayment);

            booking.setPayment(newPayment);
            bookingRepository.save(booking);

            return CreateOrderResponseDto.builder()
                    .orderId(order.get("id"))
                    .amount(amount)
                    .currency("INR")
                    .keyId(keyId)
                    .build();

        } catch (RazorpayException e) {

            throw new RuntimeException(
                    "Unable to create Razorpay order", e);
        }
    }

    @Transactional
    @Override
    public VerifyPaymentResponseDto verifyPayment(VerifyPaymentRequestDto request) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Booking booking = bookingRepository.findByPayment(payment).orElseThrow(()->
                new ResourceNotFoundException("Payment not found"));

        User currentUser = getCurrentUser();

        if(!booking.getUser().getId().equals(currentUser.getId())) {

            throw new AccessDeniedException("You are not allowed to verify this payment");
        }

        try {

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", request.getRazorpayOrderId());

            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());

            attributes.put("razorpay_signature", request.getRazorpaySignature());

            Utils.verifyPaymentSignature(attributes, keySecret);

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());

            payment.setPaymentStatus(PaymentStatus.CONFIRMED);

            paymentRepository.save(payment);

            booking.setBookingStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);

            return VerifyPaymentResponseDto.builder().message("Payment verified successfully")
                    .paymentStatus(PaymentStatus.CONFIRMED)
                    .bookingId(booking.getId())
                    .build();

        } catch (RazorpayException e) {

            payment.setPaymentStatus(PaymentStatus.CANCELLED);

            paymentRepository.save(payment);

            throw new RuntimeException("Payment verification failed", e);
        }
    }

    @Transactional
    @Override
    public void paymentFailed(PaymentFailedRequestDto request) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.CONFIRMED) {
            throw new IllegalStateException("Payment is already confirmed");
        }
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    private User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }
}
