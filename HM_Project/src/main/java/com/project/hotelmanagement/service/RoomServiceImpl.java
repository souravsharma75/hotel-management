package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.Room;
import com.project.hotelmanagement.dto.HotelDto;
import com.project.hotelmanagement.dto.RoomDto;
import com.project.hotelmanagement.exception.ResourceNotFoundException;
import com.project.hotelmanagement.repository.HotelRepository;
import com.project.hotelmanagement.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createRoom(Long hotelId, RoomDto roomDto) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id "+hotelId));

        log.info("Creating room for Hotel id {}",hotelId);

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        return modelMapper.map(room,RoomDto.class);

    }

    @Override
    public RoomDto getRoomById(Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(()->
                new ResourceNotFoundException("Room not found with Id "+roomId));
        log.info("Getting hotel room by id {}",roomId);

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoomById(Long roomId, RoomDto roomDto) {

        Room room = roomRepository.findById(roomId).orElseThrow(()->
                new ResourceNotFoundException("Room not found with Id "+roomId));

//        room.setType(roomDto.getType());
//        room.setBasePrice(roomDto.getBasePrice());
//        room.setPhotos(roomDto.getPhotos());
//        room.setAmenities(roomDto.getAmenities());
//        room.setTotalCount(roomDto.getTotalCount());
//        room.setCapacity(roomDto.getCapacity());

        modelMapper.map(roomDto,room);

        Room updatedRoom = roomRepository.save(room);

        return modelMapper.map(updatedRoom,RoomDto.class);
    }

    @Override
    public String deleteRoomById(Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(()->
                new ResourceNotFoundException("room not found with Id "+roomId));

        roomRepository.delete(room);

        return "Room deleted successfully";
    }


}
