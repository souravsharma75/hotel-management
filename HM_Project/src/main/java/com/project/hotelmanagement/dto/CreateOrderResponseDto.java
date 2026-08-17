package com.project.hotelmanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CreateOrderResponseDto {

    private String orderId;

    private BigDecimal amount;

    private String currency;

    private String keyId;
}
