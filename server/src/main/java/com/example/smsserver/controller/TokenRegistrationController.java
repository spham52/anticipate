package com.example.smsserver.controller;

import com.example.smsserver.dto.TokenRegistrationRequest;
import com.example.smsserver.model.RegistrationToken;
import com.example.smsserver.repository.TokenRegistrationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
// Rest controller used for handling Firebase token registration
public class TokenRegistrationController {
    private final TokenRegistrationRepository registrationTokenRepo;

    // The client will send tokenID (which is generated via the Firebase SDK) and other details to
    // this API endpoint. Then this endpoint will save it into the database and associate it with that user.
    // The server can then use this token to send push notifications to that specific device
    @PostMapping("/token/register")
    public ResponseEntity<Void> registerToken(@RequestBody @Valid TokenRegistrationRequest tokenRegistrationRequest) {
        RegistrationToken token = RegistrationToken.builder()
                .tokenID(tokenRegistrationRequest.getTokenID())
                .userID(tokenRegistrationRequest.getUserID())
                .platform(tokenRegistrationRequest.getPlatform())
                .appVersion(tokenRegistrationRequest.getAppVersion())
                .build();
        registrationTokenRepo.save(token);
        return ResponseEntity.ok().build();
    }
}
