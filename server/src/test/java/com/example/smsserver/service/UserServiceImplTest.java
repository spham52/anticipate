package com.example.smsserver.service;

import com.example.smsserver.dto.UserRegistrationRequest;
import com.example.smsserver.model.User;
import com.example.smsserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
        // test for when email already exists in the database
    void registerUser_throwsException_whenEmailAlreadyExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("user@example.com")
                .password("test123")
                .username("john")
                .build();

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
        // test for when username already exists in the database
    void registerUser_throwsException_whenUsernameAlreadyExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("user@example.com")
                .username("john")
                .password("test123")
                .build();
        when(userRepository.existsByUsernameIgnoreCase("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
        // test case when the UserRegistrationRequest is valid
    void registerUser_whenValidUser() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .email("user@example.com")
                .username("john")
                .password("test123")
                .build();

        when(userRepository.existsByUsernameIgnoreCase("john")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("test123")).thenReturn("{bcrypt}xyz123");

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assert (savedUser.getPassword().equals("{bcrypt}xyz123"));
        assert (savedUser.getUsername().equals("john"));
        assert (savedUser.getEmail().equals("user@example.com"));

    }
}