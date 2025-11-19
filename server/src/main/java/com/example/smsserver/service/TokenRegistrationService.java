package com.example.smsserver.service;

import com.example.smsserver.dto.TokenRegistrationRequest;
import com.example.smsserver.model.TokenRegistration;

public interface TokenRegistrationService {

    public TokenRegistration registerToken(TokenRegistrationRequest tokenRegistrationRequest);
}
