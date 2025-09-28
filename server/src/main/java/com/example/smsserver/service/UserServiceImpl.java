package com.example.smsserver.service;

import com.example.smsserver.dto.UserRegistrationRequest;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User registerUser(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRegistrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        if (userRepository.existsByUsernameIgnoreCase(userRegistrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = User.builder()
                .email(userRegistrationRequest.getEmail())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .username(userRegistrationRequest.getUsername())
                .build();

        return userRepository.save(user);
    }
}
