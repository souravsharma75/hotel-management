package com.project.hotelmanagement.strategy;

import com.project.hotelmanagement.Entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {

        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //apply additional strategy
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);


        return pricingStrategy.calculatePrice(inventory);
    }
}
