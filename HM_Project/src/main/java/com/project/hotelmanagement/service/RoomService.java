package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.RoomDto;
import org.springframework.stereotype.Service;

@Service
public interface RoomService {

    RoomDto createRoom(Long id, RoomDto roomDto);

    RoomDto getRoomById(Long id);

    RoomDto updateRoomById(Long id, RoomDto roomDto);

    String deleteRoomById(Long id);
}
