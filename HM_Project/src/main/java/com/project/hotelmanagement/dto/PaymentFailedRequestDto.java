package com.project.hotelmanagement.dto;

import lombok.Data;

@Data
public class PaymentFailedRequestDto {

    private String razorpayOrderId;

    private String razorpayPaymentId;

}
