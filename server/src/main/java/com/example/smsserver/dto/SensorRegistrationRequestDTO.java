package com.example.smsserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorRegistrationRequestDTO {
    @NotBlank
    @NotNull
    private String userID;

    @NotBlank
    @NotNull
    private String sensorID;
}
