package com.example.smsserver.service;

import com.example.smsserver.dto.TokenRegistrationRequestDTO;
import com.example.smsserver.exception.UserAlreadyExistsException;
import com.example.smsserver.model.TokenRegistration;
import com.example.smsserver.repository.TokenRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenRegistrationServiceImplTest {

    @Mock
    TokenRegistrationRepository tokenRegistrationRepository;

    @InjectMocks
    TokenRegistrationServiceImpl tokenRegistrationService;

    @Test
    void registerToken_whenValidTokenRequest() {
        String userID = "user-123";
        String tokenID = "token-123";
        String platform = "android";
        String appVersion = "1.0.0";

        TokenRegistrationRequestDTO request = TokenRegistrationRequestDTO.builder()
                .tokenID(tokenID)
                .platform(platform)
                .appVersion(appVersion)
                .build();

        TokenRegistration saved = TokenRegistration.builder()
                .userID(userID)
                .tokenID(tokenID)
                .platform(platform)
                .appVersion(appVersion)
                .build();

        when(tokenRegistrationRepository.save(any(TokenRegistration.class))).thenReturn(saved);

        TokenRegistration result = tokenRegistrationService.registerToken(request, userID);
        verify(tokenRegistrationRepository).save(any(TokenRegistration.class));

        assertEquals(userID, result.getUserID());
        assertEquals(tokenID, result.getTokenID());
    }
}