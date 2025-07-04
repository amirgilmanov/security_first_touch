package com.example.security_first_touch.controller;

import com.example.security_first_touch.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured Data";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Secured Data";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Admin Data";
    }

    @GetMapping("/info")
    public String info(Principal principal) {
        return principal.getName();
    }
}
