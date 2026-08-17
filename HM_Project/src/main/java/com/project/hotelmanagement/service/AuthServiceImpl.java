package com.project.hotelmanagement.service;

import com.project.hotelmanagement.Entity.User;
import com.project.hotelmanagement.Entity.enums.Role;
import com.project.hotelmanagement.dto.RegisterRequestDto;
import com.project.hotelmanagement.exception.EmailAlreadyExistsException;
import com.project.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequestDto requestDto) {
        registerUser(requestDto, Role.GUEST);
    }

    @Override
    public void registerHotelManager(RegisterRequestDto requestDto) {
        registerUser(requestDto, Role.HOTEL_MANAGER);
    }

    private void registerUser(RegisterRequestDto requestDto, Role role) {

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        user.setRoles(Set.of(role));

        userRepository.save(user);
    }
}
