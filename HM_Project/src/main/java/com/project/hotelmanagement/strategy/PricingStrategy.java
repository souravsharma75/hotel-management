package com.project.hotelmanagement.strategy;

import com.project.hotelmanagement.Entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
