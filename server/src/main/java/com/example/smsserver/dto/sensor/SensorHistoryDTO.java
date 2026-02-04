package com.example.smsserver.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

// DTO used to return a SensorNotification entity to frontend
@Data
@AllArgsConstructor
public class SensorHistoryDTO {

    private Long id;

    private String sensorID;

    private Instant timestamp;

}
