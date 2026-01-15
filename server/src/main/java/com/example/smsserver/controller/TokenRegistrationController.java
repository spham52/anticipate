package com.example.smsserver.controller;

import com.example.smsserver.dto.TokenRegistrationRequestDTO;
import com.example.smsserver.service.TokenRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/token")
@RequiredArgsConstructor
// Rest controller used for handling Firebase token registration (Firebase Cloud Messaging, not auth)
public class TokenRegistrationController {
    private final TokenRegistrationService registrationTokenService;

    // The client will send tokenID (which is generated via the Firebase SDK) and other details to
    // this API endpoint. Then this endpoint will save it into the database and associate it with that user.
    // The server can then use this token to send push notifications to that specific device
    @PostMapping("/register")
    public ResponseEntity<String> registerToken(@RequestBody @Valid
                                                TokenRegistrationRequestDTO tokenRegistrationRequestDTO,
                                                @AuthenticationPrincipal String userID) {
        registrationTokenService.registerToken(tokenRegistrationRequestDTO, userID);
        return ResponseEntity.ok("Token registered successfully");
    }
}
