package com.project.hotelmanagement.dto;

import com.project.hotelmanagement.Entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerifyPaymentResponseDto {

    private String message;

    private PaymentStatus paymentStatus;

    private Long bookingId;
}
