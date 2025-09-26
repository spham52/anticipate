package com.example.smsserver.controller;

import com.example.smsserver.dto.RegistrationRequest;
import com.example.smsserver.model.RegistrationToken;
import com.example.smsserver.repository.RegistrationTokenRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationTokenRepository registrationTokenRepo;

    // The client will send tokenID (which is generated via the Firebase SDK) and other details to
    // this API endpoint. Then this endpoint will save it into the database and associate it with that user.
    // The server can then use this token to send push notifications to that specific device
    @PostMapping("/token/register")
    public ResponseEntity<Void> registerToken(@RequestBody @Valid RegistrationRequest registrationRequest) {
        RegistrationToken token = RegistrationToken.builder()
                .tokenID(registrationRequest.getTokenID())
                .userID(registrationRequest.getUserID())
                .platform(registrationRequest.getPlatform())
                .appVersion(registrationRequest.getAppVersion())
                .build();
        registrationTokenRepo.save(token);
        return ResponseEntity.ok().build();
    }
}
