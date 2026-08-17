package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.HotelDto;
import com.project.hotelmanagement.dto.HotelInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    List<HotelDto> getAllHotels();

    List<HotelDto> getAllHotelsForAdmin();

    String deleteHotelById(Long id);

    HotelDto activateHotel(Long id);

    HotelDto deactivateHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);

}
