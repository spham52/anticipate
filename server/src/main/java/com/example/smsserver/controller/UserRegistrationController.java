package com.example.smsserver.controller;

import com.example.smsserver.dto.UserRegistrationRequest;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.UserRepository;
import com.example.smsserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
// REST Controller class for handling user registration requests
public class UserRegistrationController {
    private final UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationRequest userRegistration) {
        userService.registerUser(userRegistration);
        return ResponseEntity.ok("User registered successfully");
    }
}
