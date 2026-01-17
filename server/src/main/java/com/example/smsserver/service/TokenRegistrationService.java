package com.example.smsserver.service;

import com.example.smsserver.dto.Token.TokenRegistrationRequestDTO;
import com.example.smsserver.model.TokenRegistration;

public interface TokenRegistrationService {

    TokenRegistration registerToken(TokenRegistrationRequestDTO tokenRegistrationRequestDTO, String userID);
}
