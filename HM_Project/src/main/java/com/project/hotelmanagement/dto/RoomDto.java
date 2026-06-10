package com.project.hotelmanagement.dto;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.enums.RoomType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RoomDto {

    private Long id;

    private RoomType type;

    private BigDecimal basePrice;

    private Set<String> photos;

    private Set<String> amenities;

    private Integer totalCount;

    private Integer capacity;

}
