package com.example.smsserver.dto.sensor;

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
