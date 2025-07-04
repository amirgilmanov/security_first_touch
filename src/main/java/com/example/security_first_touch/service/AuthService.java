package com.example.security_first_touch.service;

import com.example.security_first_touch.domain.dto.JwtRequest;
import com.example.security_first_touch.domain.dto.JwtResponse;
import com.example.security_first_touch.domain.dto.RegistrationUserDto;
import com.example.security_first_touch.domain.dto.UserDto;
import com.example.security_first_touch.domain.entity.User;
import com.example.security_first_touch.exception.AppError;
import com.example.security_first_touch.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(UNAUTHORIZED.value(), "Неправильный логин или пароль"), UNAUTHORIZED);
        }
        UserDetails user = userService.loadUserByUsername(authRequest.username());
        String jwtToken = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto dto) {
        if (!Objects.equals(dto.password(), dto.confirmPassword())) {
            return new ResponseEntity<>(new AppError(CONFLICT.value(), "Пароли не совпадают"), CONFLICT);
        }

        if (userService.findByUsername(dto.name()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(), "Пользователь с таким именем уже существует"), CONFLICT);
        }

        User user = userService.createUser(dto);
        return ResponseEntity.ok(new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        ));
    }
}
