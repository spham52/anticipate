package com.example.smsserver.service;

import com.example.smsserver.dto.Sensor.SensorHistoryDTO;
import com.example.smsserver.dto.Sensor.SensorNotificationDTO;
import com.example.smsserver.dto.Sensor.SensorRegistrationRequestDTO;
import com.example.smsserver.dto.Sensor.SensorResponseDTO;
import com.example.smsserver.model.Sensor;
import com.example.smsserver.model.SensorNotification;
import com.example.smsserver.model.User;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface SensorService {

    void associateUserWithSensor(SensorRegistrationRequestDTO request, String userID);

    List<SensorNotification> findAllNotificationsBySensor(String sensorID, String userID);

    void saveNotification(SensorNotificationDTO notification);

    Sensor findSensorById(String sensorId);

    List<Sensor> findSensorsByUser(String userID);

    List<SensorResponseDTO> findSensorsDTOByUser(String userID);
    List<SensorHistoryDTO> findAllNotificationsDTOBySensor(String sensorID, String userID);
    Page<SensorHistoryDTO> findAllNotificationsBySensorPageable(int page, int size,
                                                                      String sensorID, String userID);
    List<SensorHistoryDTO> findAllNotificationsDTOHourAggregateByDate(
            LocalDate date, String timezone, String sensorID, String userID);
}
