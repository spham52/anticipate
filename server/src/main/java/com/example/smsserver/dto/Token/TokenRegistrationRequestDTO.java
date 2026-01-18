package com.example.smsserver.dto.Token;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRegistrationRequestDTO {
    @NotBlank
    private String tokenID;

    @NotBlank
    private String platform;

    @NotBlank
    private String appVersion;
}
