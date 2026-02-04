package com.example.smsserver.dto.captcha;

// DTO for Google's Recaptcha response

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecaptchaResponse {
    private boolean success;
}
