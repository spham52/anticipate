package com.example.smsserver.dto;

// DTO for returning sensor information to user

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SensorResponseDTO {
    private String id;
}
