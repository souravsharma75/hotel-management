package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotel(Hotel hotel);
}
