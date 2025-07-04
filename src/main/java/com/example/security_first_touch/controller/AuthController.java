package com.example.security_first_touch.controller;

import com.example.security_first_touch.domain.dto.JwtRequest;
import com.example.security_first_touch.domain.dto.RegistrationUserDto;
import com.example.security_first_touch.service.AuthService;
import com.example.security_first_touch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return ResponseEntity.ok(authService.createAuthToken(authRequest));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto dto) {
        log.debug("Создание нового пользователя {}", dto.name());
       return ResponseEntity.ok(authService.createNewUser(dto));
    }
}
