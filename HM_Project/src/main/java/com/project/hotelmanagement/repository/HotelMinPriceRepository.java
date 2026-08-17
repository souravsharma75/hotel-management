package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.HotelMinPrice;
import com.project.hotelmanagement.dto.HotelPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);

    @Query("""
            select new com.project.hotelmanagement.dto.HotelPriceDto(i.hotel.id,MIN(i.price))
            from HotelMinPrice i
            where i.hotel.city = :city
            AND i.hotel.active = true
            AND i.date between :startDate AND :endDate
            group by i.hotel.id
            """)
    Page<HotelPriceDto> findHotelWithAvailableInventory(String city, LocalDate startDate, LocalDate endDate, Integer roomsCount, long dateCount, Pageable pageable);
}
