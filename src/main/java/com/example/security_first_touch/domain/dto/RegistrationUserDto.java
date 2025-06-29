package com.example.security_first_touch.domain.dto;

import lombok.Value;

/**
 * DTO for {@link com.example.security_first_touch.domain.entity.User}
 */

public record RegistrationUserDto(String name, String password, String confirmPassword, String email) {
}
