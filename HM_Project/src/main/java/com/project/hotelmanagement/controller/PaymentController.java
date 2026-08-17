package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.CreateOrderResponseDto;
import com.project.hotelmanagement.dto.PaymentFailedRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentRequestDto;
import com.project.hotelmanagement.dto.VerifyPaymentResponseDto;
import com.project.hotelmanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
@PreAuthorize("hasRole('GUEST')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<CreateOrderResponseDto> createOrder(@PathVariable Long bookingId) {

        return ResponseEntity.ok(paymentService.createOrder(bookingId));
    }

    @PostMapping("verify")
    public ResponseEntity<VerifyPaymentResponseDto> verifyPayment(@RequestBody VerifyPaymentRequestDto request) {

        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> paymentFailed (@RequestBody PaymentFailedRequestDto request) {

        paymentService.paymentFailed(request);

        return ResponseEntity.ok().build();
    }
}
