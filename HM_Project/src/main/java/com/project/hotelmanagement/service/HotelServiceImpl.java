package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.Room;
import com.project.hotelmanagement.dto.HotelDto;
import com.project.hotelmanagement.dto.HotelInfoDto;
import com.project.hotelmanagement.dto.RoomDto;
import com.project.hotelmanagement.exception.ResourceNotFoundException;
import com.project.hotelmanagement.repository.HotelRepository;
import com.project.hotelmanagement.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    private final ModelMapper modelMapper;

    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name :{}",hotelDto.getName());

        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);

        log.info("Create a new Hotel with id :{}",hotel.getId());
        return modelMapper.map(hotel,HotelDto.class);

    }

    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel details by id :{}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id "+id));

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id "+id));

        modelMapper.map(hotelDto, hotel);
        Hotel updatedHotel = hotelRepository.save(hotel);

        return modelMapper.map(updatedHotel, HotelDto.class);
    }

    @Override
    public List<HotelDto> getAllHotels() {

        List<Hotel> hotels = hotelRepository.findByActiveTrue();

        return hotels.stream().map(hotel ->
                modelMapper.map(hotel, HotelDto.class)).toList();
    }

    @Override
    public List<HotelDto> getAllHotelsForAdmin() {

        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream().map(hotel ->
                modelMapper.map(hotel, HotelDto.class)).toList();
    }


    @Override
    public String deleteHotelById(Long id) {

        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id "+id));

        hotel.setActive(false);     // Soft delete
        hotelRepository.save(hotel);

        return "Hotel deleted successfully";

//        hotelRepository.delete(hotel);    // Hard Delete
//        return "Hotel deleted successfully";

    }

    @Override
    public HotelDto activateHotel(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with Id " + id));

        hotel.setActive(true);

        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto deactivateHotel(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Hotel not found with Id "+id));
        hotel.setActive(false);

        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }


    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {

        log.info("Searching the hotel with Id {}",hotelId);

        Hotel hotel = hotelRepository.findByIdAndActiveTrue(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id "+hotelId));

        HotelDto hotelDto = modelMapper.map(hotel, HotelDto.class);

        List<Room> rooms = roomRepository.findByHotel(hotel);

        List<RoomDto> roomDto = rooms.stream().map(room ->
                modelMapper.map(room, RoomDto.class)).toList();

        return new HotelInfoDto(hotelDto, roomDto);
    }
}
