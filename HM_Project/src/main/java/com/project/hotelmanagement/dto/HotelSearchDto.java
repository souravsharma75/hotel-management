package com.project.hotelmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HotelSearchDto {

    private Long id;

    private String name;

    private String city;

    private List<String> amenities;

    private List<String> photos;

    private BigDecimal minPrice;
}
