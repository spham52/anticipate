package com.example.smsserver.dto.Sensor;

import com.example.smsserver.model.Sensor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// DTO used to return a SensorNotification entity to frontend
@Data
@AllArgsConstructor
public class SensorHistoryDTO {

    private Long id;

    private String sensorID;

    private LocalDateTime timestamp;

}
