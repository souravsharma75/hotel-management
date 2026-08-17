package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.LoginRequestDto;
import com.project.hotelmanagement.dto.RegisterRequestDto;
import com.project.hotelmanagement.security.JwtService;
import com.project.hotelmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        ));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto requestDto) {
        authService.register(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    @PostMapping("/register/hotel-manager")
    public ResponseEntity<String> registerHotelManager(@RequestBody @Valid RegisterRequestDto requestDto) {
        authService.registerHotelManager(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Hotel Manager Register Successfully");
    }
}
