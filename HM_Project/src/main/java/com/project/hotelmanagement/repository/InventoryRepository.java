package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.Inventory;
import com.project.hotelmanagement.Entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    @Query("""
            select distinct i.hotel
            from Inventory i
            where i.hotel.city = :city
            AND i.hotel.active = true
            AND i.date between :startDate AND :endDate
            AND i.closed = false
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            group by i.hotel, i.room
            Having count(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(String city, LocalDate startDate, LocalDate endDate, Integer roomsCount, long dateCount, Pageable pageable);

    @Query("""
            select i 
            from Inventory i
            where i.room.id = :roomId
            AND i.date between :checkIn AND :checkOut
            AND i.closed = false
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("roomCount") Integer roomCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    List<Inventory> findByRoomIdAndDateBetween(Long roomId, LocalDate checkIn, LocalDate checkOut);
}
