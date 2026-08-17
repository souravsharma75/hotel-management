package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.RegisterRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void register(RegisterRequestDto requestDto);

    void registerHotelManager(RegisterRequestDto requestDto);
}
