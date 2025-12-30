package com.scaler.userservice.controller;

import com.scaler.userservice.dto.UserResponse;
import com.scaler.userservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getProfile(email);
    }

    @PutMapping("/me")
    public UserResponse updateProfile(
            Authentication authentication,
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        String email = authentication.getName();
        return userService.updateProfile(email, firstName, lastName);
    }
}
