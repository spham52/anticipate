package com.example.smsserver.dto.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecaptchaRequest {
    private String secret;
    private String response;
}
