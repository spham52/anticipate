package com.example.smsserver.controller;

import com.example.smsserver.dto.User.UserRegistrationRequestDTO;
import com.example.smsserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
// REST Controller class for handling user registration requests
public class UserRegistrationController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationRequestDTO userRegistration) {
        userService.registerUser(userRegistration);
        return ResponseEntity.ok("User registered successfully");
    }
}
