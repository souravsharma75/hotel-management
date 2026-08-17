package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.*;
import com.project.hotelmanagement.service.HotelService;
import com.project.hotelmanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @PostMapping("/search")
    public ResponseEntity<PageResponse<HotelSearchDto>> searchHotels(@RequestBody HotelSearchReqDto hotelSearchReqDto) {

        return ResponseEntity.ok(inventoryService.searchHotels(hotelSearchReqDto));

    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {

        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
