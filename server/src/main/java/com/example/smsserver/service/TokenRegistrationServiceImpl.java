package com.example.smsserver.service;

import com.example.smsserver.dto.Token.TokenRegistrationRequestDTO;
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
        TokenRegistration token = TokenRegistration.builder()
                .tokenID(tokenRegistrationRequestDTO.getTokenID())
                .userID(userID)
                .platform(tokenRegistrationRequestDTO.getPlatform())
                .appVersion(tokenRegistrationRequestDTO.getAppVersion())
                .build();
        return tokenRegistrationRepository.save(token);
    }
}
