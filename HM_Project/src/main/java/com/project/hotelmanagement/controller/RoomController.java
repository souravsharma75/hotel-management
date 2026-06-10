package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.RoomDto;
import com.project.hotelmanagement.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        log.info("Getting hotel room by id {}",roomId);

        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @PutMapping("/{roomId}")
    ResponseEntity<RoomDto> updateRoomById(@PathVariable Long roomId, @Valid @RequestBody RoomDto roomDto) {
        log.info("Updating hotel room by id {}",roomId);

        RoomDto updateRoom = roomService.updateRoomById(roomId, roomDto);

        return ResponseEntity.ok(updateRoom);
    }

    @DeleteMapping("/{roomId}")
    ResponseEntity<String> deleteRoomById(@PathVariable Long roomId) {
        log.info("Deleting hotel room by id {}",roomId);

        String message = roomService.deleteRoomById(roomId);

        return ResponseEntity.ok(message);
    }

}
