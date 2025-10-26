package com.example.smsserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRegistrationRequest {
    @NotBlank
    private String tokenID;

    @NotBlank
    private String userID;

    @NotBlank
    private String platform;

    @NotBlank
    private String appVersion;
}
