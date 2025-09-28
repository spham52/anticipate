package com.example.smsserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// SensorNotification is a POJO class which represents the JSON request the Pico device will
// send to the server to alert that motion has been detected
public class SensorNotification {
    @NotBlank
    private String sensorID;
}
