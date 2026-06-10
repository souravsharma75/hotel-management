package com.project.hotelmanagement.Entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class HotelContactInfo {

    private String address;

    private String phoneNo;

    private String email;

}
