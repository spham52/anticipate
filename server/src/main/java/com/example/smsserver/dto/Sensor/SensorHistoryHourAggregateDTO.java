package com.example.smsserver.dto.Sensor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorHistoryHourAggregateDTO {
    int hour;
    int count;
}
