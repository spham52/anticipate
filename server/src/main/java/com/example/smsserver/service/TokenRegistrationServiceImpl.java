package com.example.smsserver.service;

import com.example.smsserver.dto.TokenRegistrationRequest;
import com.example.smsserver.exception.UserAlreadyExistsException;
import com.example.smsserver.model.TokenRegistration;
import com.example.smsserver.repository.TokenRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// class used to register a Firebase token associated with a deviceID into DB
public class TokenRegistrationServiceImpl implements TokenRegistrationService {
    private final TokenRegistrationRepository tokenRegistrationRepository;

    @Override
    // register Firebase token and associate with userID
    // if user already has token registered, request is rejected to avoid duplicates
    public TokenRegistration registerToken(TokenRegistrationRequest tokenRegistrationRequest) {
        if (tokenRegistrationRepository.existsByUserID(tokenRegistrationRequest.getUserID())) {
            throw new UserAlreadyExistsException(tokenRegistrationRequest.getUserID());
        }

        TokenRegistration token = TokenRegistration.builder()
                .tokenID(tokenRegistrationRequest.getTokenID())
                .userID(tokenRegistrationRequest.getUserID())
                .platform(tokenRegistrationRequest.getPlatform())
                .appVersion(tokenRegistrationRequest.getAppVersion())
                .build();
        return tokenRegistrationRepository.save(token);
    }
}
