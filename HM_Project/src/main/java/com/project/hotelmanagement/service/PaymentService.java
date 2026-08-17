package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.CreateOrderResponseDto;

import com.project.hotelmanagement.dto.PaymentFailedRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    CreateOrderResponseDto createOrder(Long bookingId);

    VerifyPaymentResponseDto verifyPayment(VerifyPaymentRequestDto request);

    void paymentFailed(PaymentFailedRequestDto request);
}
