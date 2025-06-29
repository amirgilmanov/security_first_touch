package com.example.security_first_touch.domain.dto;

public record JwtRequest(
        String username,
        String password
) {
}
