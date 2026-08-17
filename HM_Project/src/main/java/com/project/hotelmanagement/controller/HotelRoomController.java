package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.RoomDto;
import com.project.hotelmanagement.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/hotels")
@PreAuthorize("hasRole('HOTEL_MANAGER')")
public class HotelRoomController {

    private final RoomService roomService;

    @PostMapping("/{hotelId}/rooms")
    ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId, @Valid @RequestBody RoomDto roomDto) {
        log.info("Attempting to create room for hotel id {}",hotelId);

        RoomDto createdRoom = roomService.createRoom(hotelId, roomDto);

        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }
}
