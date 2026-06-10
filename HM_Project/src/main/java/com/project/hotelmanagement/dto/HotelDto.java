package com.project.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class HotelDto {

    private Long id;

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private Set<String> photos;

    private Set<String> amenities;

    private HotelContactInfoDto hotelContactInfo;

    private Boolean active;


}
