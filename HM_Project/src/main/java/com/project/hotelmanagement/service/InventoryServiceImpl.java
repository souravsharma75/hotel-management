package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.Hotel;
import com.project.hotelmanagement.Entity.Inventory;
import com.project.hotelmanagement.Entity.Room;
import com.project.hotelmanagement.dto.*;
import com.project.hotelmanagement.repository.HotelMinPriceRepository;
import com.project.hotelmanagement.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {
    private final HotelMinPriceRepository hotelMinPriceRepository;

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeInventoryForAYear(Room room) {

        log.info("Initializing inventory for room {}", room.getId());

        List<Inventory> inventories = new ArrayList<>();

        for (int i = 0; i < 365; i++) {
            LocalDate inventoryDate = LocalDate.now().plusDays(i);

            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .date(inventoryDate)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventories.add(inventory);
        }
        inventoryRepository.saveAll(inventories);

        log.info("Created {} inventory records", inventories.size());
    }

    @Override
    public void deleteInventoriesByRoom(Room room) {

        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public PageResponse<HotelSearchDto> searchHotels(HotelSearchReqDto hotelSearchReqDto) {

        log.info("Searching for Hotel {} city, from {} to {}",
                hotelSearchReqDto.getCity(),
                hotelSearchReqDto.getStartDate(),
                hotelSearchReqDto.getEndDate());

        Pageable pageable = PageRequest.of(
                hotelSearchReqDto.getPage(),
                hotelSearchReqDto.getSize());

        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchReqDto.getStartDate(),
                hotelSearchReqDto.getEndDate()) + 1;

        Page<Hotel> hotelInfo = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchReqDto.getCity(),
                hotelSearchReqDto.getStartDate(),
                hotelSearchReqDto.getEndDate(),
                hotelSearchReqDto.getRoomsCount(),
                dateCount,
                pageable
        );

        Page<HotelPriceDto> hotelPage = hotelMinPriceRepository.findHotelWithAvailableInventory(hotelSearchReqDto.getCity(),
                hotelSearchReqDto.getStartDate(), hotelSearchReqDto.getEndDate(), hotelSearchReqDto.getRoomsCount(),
                dateCount, pageable);

        Map<Long, BigDecimal> hotelPriceMap = hotelPage.getContent()
                .stream()
                .collect(Collectors.toMap(HotelPriceDto::getHotelId, HotelPriceDto::getPrice));


        List<HotelSearchDto> hotels = hotelInfo.getContent()
                .stream().map((e) -> {
                    HotelSearchDto dto =
                            modelMapper.map(e, HotelSearchDto.class);
                    dto.setMinPrice(hotelPriceMap.getOrDefault(e.getId(),BigDecimal.ZERO));

                    return dto;

                }).toList();

        return new PageResponse<>(hotels, hotelInfo.getTotalElements(),hotelInfo.getTotalPages());
    }
}
