package com.example.smsserver.dto.Sensor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
// DTO for returning sensor information to user
public class SensorResponseDTO {
    private String id;
}
