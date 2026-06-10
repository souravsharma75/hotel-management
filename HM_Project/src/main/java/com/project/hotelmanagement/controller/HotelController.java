package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.HotelDto;
import com.project.hotelmanagement.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/hotels")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto hotelDto) {
        log.info("Attempting to create new hotel with name : {} ",hotelDto.getName());
        HotelDto hoteldto = hotelService.createNewHotel(hotelDto);

        return new ResponseEntity<>(hoteldto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        HotelDto hoteldto = hotelService.getHotelById(id);

        return ResponseEntity.ok(hoteldto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id, @Valid @RequestBody HotelDto hotelDto) {
        HotelDto updateHotel = hotelService.updateHotelById(id, hotelDto);

        return ResponseEntity.ok(updateHotel);

    }
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotels = hotelService.getAllHotels();

        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/all/data")
    public ResponseEntity<List<HotelDto>> getAllHotelsForAdmin() {

        return ResponseEntity.ok(hotelService.getAllHotelsForAdmin());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotelById(@PathVariable Long id) {
        String message = hotelService.deleteHotelById(id);

        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long id) {
        HotelDto hoteldto = hotelService.activateHotel(id);

        return ResponseEntity.ok(hoteldto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<HotelDto> deactivateHotel(@PathVariable Long id) {
        HotelDto hoteldto = hotelService.deactivateHotel(id);

        return ResponseEntity.ok(hoteldto);
    }


}
