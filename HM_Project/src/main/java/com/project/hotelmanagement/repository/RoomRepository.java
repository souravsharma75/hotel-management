package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
