package com.example.smsserver.service;

import com.example.smsserver.dto.token.TokenRegistrationRequestDTO;
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
    public TokenRegistration registerToken(TokenRegistrationRequestDTO tokenRegistrationRequestDTO, String userID) {
        TokenRegistration existing = tokenRegistrationRepository.findByUserID(userID);

        // checks if token exists first. if it does, it updates the existing token instead of creating
        // a new one
        if (existing != null) {
            existing.setTokenID(tokenRegistrationRequestDTO.getTokenID());
            existing.setPlatform(tokenRegistrationRequestDTO.getPlatform());
            existing.setAppVersion(tokenRegistrationRequestDTO.getAppVersion());
            return tokenRegistrationRepository.save(existing);
        }

        TokenRegistration token = TokenRegistration.builder()
                .tokenID(tokenRegistrationRequestDTO.getTokenID())
                .userID(userID)
                .platform(tokenRegistrationRequestDTO.getPlatform())
                .appVersion(tokenRegistrationRequestDTO.getAppVersion())
                .build();
        return tokenRegistrationRepository.save(token);
    }
}
