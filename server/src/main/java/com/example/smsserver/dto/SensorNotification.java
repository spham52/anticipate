package com.example.smsserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorNotification {
    @NotBlank
    private String sensorID;
}
