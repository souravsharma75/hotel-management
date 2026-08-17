package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Room;
import com.project.hotelmanagement.dto.HotelSearchDto;
import com.project.hotelmanagement.dto.HotelSearchReqDto;
import com.project.hotelmanagement.dto.PageResponse;
import org.springframework.stereotype.Service;

@Service
public interface InventoryService {

    void initializeInventoryForAYear(Room room);

    void deleteInventoriesByRoom(Room room);

    PageResponse<HotelSearchDto> searchHotels(HotelSearchReqDto hotelSearchReqDto);
}
